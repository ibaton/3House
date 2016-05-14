package se.treehou.ng.ohcommunicator.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Realm;

import org.atmosphere.wasync.Client;
import org.atmosphere.wasync.ClientFactory;
import org.atmosphere.wasync.Decoder;
import org.atmosphere.wasync.Encoder;
import org.atmosphere.wasync.Event;
import org.atmosphere.wasync.Function;
import org.atmosphere.wasync.OptionsBuilder;
import org.atmosphere.wasync.Request;
import org.atmosphere.wasync.RequestBuilder;
import org.atmosphere.wasync.Socket;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import se.treehou.ng.ohcommunicator.connector.BasicAuthServiceGenerator;
import se.treehou.ng.ohcommunicator.connector.ConnectorUtil;
import se.treehou.ng.ohcommunicator.connector.Constants;
import se.treehou.ng.ohcommunicator.connector.GsonHelper;
import se.treehou.ng.ohcommunicator.connector.OpenHabService;
import se.treehou.ng.ohcommunicator.connector.TrustModifier;
import se.treehou.ng.ohcommunicator.connector.models.OHBinding;
import se.treehou.ng.ohcommunicator.connector.models.OHInboxItem;
import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback;
import se.treehou.ng.ohcommunicator.services.callbacks.OHResponse;

public class Connector {

    private static final String TAG = Connector.class.getSimpleName();

    private Context context;

    public Connector(Context context) {
        this.context = context;
    }

    public static OpenHabService generateOpenHabService(OHServer server, String url){
        return BasicAuthServiceGenerator.createService(OpenHabService.class, server.getUsername(), server.getPassword(), url);
    }

    public ServerHandler getServerHandler(OHServer server){
        return new ServerHandler(server, context);
    }

    public static class ServerHandler {

        private OHServer server;
        private Context context;

        private OpenHabService openHabService;

        public ServerHandler(OHServer server, Context context) {
            this.server = server;
            this.context = context;

            openHabService = generateOpenHabService(server, getUrl());
        }

        public void requestBindings(final OHCallback<List<OHBinding>> bindingCallback){
            OpenHabService service = getService();
            if(service == null || bindingCallback == null) return;

            service.listBindings().enqueue(new Callback<List<OHBinding>>() {
                @Override
                public void onResponse(Call<List<OHBinding>> call, Response<List<OHBinding>> response) {
                    List<OHBinding> items = response.body();
                    if(items == null) items = new ArrayList<>();
                    bindingCallback.onUpdate(new OHResponse.Builder<>(items).build());
                }

                @Override
                public void onFailure(Call<List<OHBinding>> call, Throwable t) {
                    bindingCallback.onError();
                }
            });
        }

        /**
         * Ask server for inbox items.
         *
         * @param inboxCallback server response callback.
         */
        public void requestInboxItems(final OHCallback<List<OHInboxItem>> inboxCallback){
            OpenHabService service = getService();
            if(service == null || inboxCallback == null) return;

            service.listInboxItems().enqueue(new Callback<List<OHInboxItem>>() {
                @Override
                public void onResponse(Call<List<OHInboxItem>> call, Response<List<OHInboxItem>> response) {
                    List<OHInboxItem> inboxItems = response.body();
                    if(inboxItems == null) inboxItems = new ArrayList<>();
                    inboxCallback.onUpdate(new OHResponse.Builder<>(inboxItems).build());
                }

                @Override
                public void onFailure(Call<List<OHInboxItem>> call, Throwable t) {
                    inboxCallback.onError();
                }
            });
        }

        public void requestItem(String itemName, final OHCallback<OHItem> itemCallback){
            OpenHabService service = getService();
            if(service == null || itemCallback == null) return;

            service.getItem(itemName).enqueue(
            new Callback<OHItem>() {
                  @Override
                  public void onResponse(Call<OHItem> call, Response<OHItem> response) {
                      itemCallback.onUpdate(new OHResponse.Builder<>(response.body()).build());
                  }

                  @Override
                  public void onFailure(Call<OHItem> call, Throwable t) {
                        itemCallback.onError();
                  }
              });

        }

        public AsyncTask<Void, Void, Void> requestPageUpdates(final OHServer server, final OHLinkedPage page, final OHCallback<OHLinkedPage> callback) {
            final Socket[] pollSocket = new Socket[1];
            final AsyncHttpClient[] asyncHttpClient = new AsyncHttpClient[1];
            return new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {

                    com.ning.http.client.Realm clientRealm = null;
                    if (server.requiresAuth()) {
                        clientRealm = new com.ning.http.client.Realm.RealmBuilder()
                                .setPrincipal(server.getUsername())
                                .setPassword(server.getPassword())
                                .setUsePreemptiveAuth(true)
                                .setScheme(com.ning.http.client.Realm.AuthScheme.BASIC)
                                .build();
                    }

                    asyncHttpClient[0] = new AsyncHttpClient(
                            new AsyncHttpClientConfig.Builder().setAcceptAnyCertificate(true)
                                    .setHostnameVerifier(new TrustModifier.NullHostNameVerifier())
                                    .setRealm(clientRealm)
                                    .build()
                    );

                    Client client = ClientFactory.getDefault().newClient();
                    OptionsBuilder optBuilder = client.newOptionsBuilder().runtime(asyncHttpClient[0]);

                    UUID atmosphereId = UUID.randomUUID();

                    RequestBuilder request = client.newRequestBuilder()
                            .method(org.atmosphere.wasync.Request.METHOD.GET)
                            .uri(page.getLink())
                            .header("Accept", "application/json")
                            .header("Accept-Charset", "utf-8")
                            .header("X-Atmosphere-Transport", "long-polling")
                            .header("X-Atmosphere-tracking-id", atmosphereId.toString())
                            .encoder(new Encoder<String, Reader>() {        // Stream the request body
                                @Override
                                public Reader encode(String s) {
                                    Log.d(TAG, "wasync RequestBuilder encode");
                                    return new StringReader(s);
                                }
                            })
                            .decoder(new Decoder<String, OHLinkedPage>() {
                                @Override
                                public OHLinkedPage decode(Event e, String s) {
                                    Log.d(TAG, "wasync Decoder " + s);
                                    Gson gson = GsonHelper.createGsonBuilder();
                                    return gson.fromJson(s, OHLinkedPage.class);
                                }
                            })
                            .transport(org.atmosphere.wasync.Request.TRANSPORT.LONG_POLLING);                    // Fallback to Long-Polling

                    if (server.requiresAuth()) {
                        request.header(Constants.HEADER_AUTHENTICATION, ConnectorUtil.createAuthValue(server.getUsername(), server.getPassword()));
                    }

                    pollSocket[0] = client.create(optBuilder.build());
                    try {
                        Log.d(TAG, "wasync Socket " + pollSocket[0] + " " + request.uri());
                        pollSocket[0].on(new Function<OHLinkedPage>() {
                            @Override
                            public void on(OHLinkedPage page) {
                                Log.d(TAG, "wasync Socket received");
                                callback.onUpdate(new OHResponse.Builder<>(page).build());
                            }
                        })
                                .open(request.build());
                    } catch (IOException | ExceptionInInitializerError e) {
                        Log.d(TAG, "wasync Got error " + e);
                    }

                    Log.d(TAG, "wasync Poller started");

                    return null;
                }

                @Override
                protected void onCancelled() {
                    super.onCancelled();

                    if (pollSocket[0] != null) {
                        pollSocket[0].close();
                    }
                    if (asyncHttpClient[0] != null) {
                        asyncHttpClient[0].close();
                    }
                }
            };
        }

        public void requestItem(final OHCallback<List<OHItem>> itemCallback){
            OpenHabService service = getService();
            if(service == null || itemCallback == null){
                return;
            }

            service.listItems().enqueue(new Callback<List<OHItem>>() {
                @Override
                public void onResponse(Call<List<OHItem>> call, Response<List<OHItem>> response) {
                    for(OHItem item : response.body()) item.setServer(server);
                    itemCallback.onUpdate(new OHResponse.Builder<>(response.body()).build());
                }

                @Override
                public void onFailure(Call<List<OHItem>> call, Throwable t) {
                    itemCallback.onError();
                }
            });
        }

        /**
         * Request page for from server
         *
         * @param page the page to fetch.
         * @param responseListener response listener.
         */
        public void requestPage(OHLinkedPage page, final OHCallback<OHLinkedPage> responseListener) {
            OpenHabService service = getService();
            service.getPage(page.getLink()).enqueue(new Callback<OHLinkedPage>() {
                @Override
                public void onResponse(Call<OHLinkedPage> call, Response<OHLinkedPage> response) {
                    Log.d(TAG, "Received page " + response.message());
                    responseListener.onUpdate(new OHResponse.Builder<>(response.body()).build());
                }

                @Override
                public void onFailure(Call<OHLinkedPage> call, Throwable t) {
                    Log.e(TAG, "Received page error ", t);
                    responseListener.onError();
                }
            });
        }

        /**
         * Request page for from server
         *
         * @param page the page to fetch.
         * @return observable for page
         */
        public Observable<OHLinkedPage> requestPageRx(OHLinkedPage page) {
            OpenHabService service = getService();
            return service.getPageRx(page.getLink());
        }

        /**
         * Get url from server.
         *
         * @return url for server.
         */
        public String getUrl(){
            return getUrl(context, server);
        }

        /**
         * Get url from server.
         * @param context calling context.
         * @param server the server to connect to.
         * @return
         */
        public static String getUrl(Context context, OHServer server){
            String url = server.getLocalUrl();
            String remoteUrl = server.getRemoteUrl();
            NetworkInfo networkInfo = getNetworkInfo(context);
            if(networkInfo == null || !networkInfo.isConnected()){
                return "";
            }
            if(!isConnectedWifi(context) || !ConnectorUtil.isValidServerUrl(url)){
                url = remoteUrl;
            }

            if(url == null) url = "";

            return url;
        }

        private OpenHabService getService(){
            openHabService = generateOpenHabService(server, getUrl());

            return openHabService;
        }

        /**
         * Get network information.
         *
         * @param context the calling context.
         * @return information for the network.
         */
        public static NetworkInfo getNetworkInfo(Context context){
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo();
        }

        /**
         * Check if device is connected to network.
         *
         * @param context the calling context.
         * @return true if connected to wifi.
         */
        public static boolean isConnectedWifi(Context context){
            NetworkInfo info = getNetworkInfo(context);
            return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
        }

        /**
         * Send an approve request to inbox.
         * Update all listeners.
         *
         * @param inboxItem the inbox item to approve.
         */
        public void approveInboxItem (OHInboxItem inboxItem){
            OpenHabService service = getService();
            if(service == null) return;

            service.approveInboxItems(inboxItem.getThingUID()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
        }

        /**
         * Send an ignore request to inbox.
         * Update all listeners.
         *
         * @param inboxItem the inbox item to ignore.
         */
        public void ignoreInboxItem(OHInboxItem inboxItem){
            OpenHabService service = getService();
            if(service == null) return;

            service.ignoreInboxItems(inboxItem.getThingUID()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
            inboxItem.setFlag(OHInboxItem.FLAG_IGNORED);
        }

        /**
         * Send an unignore request to inbox.
         * Update all listeners.
         *
         * @param inboxItem the inbox item to unignore.
         */
        public void unignoreInboxItem(OHInboxItem inboxItem){
            OpenHabService service = getService();
            if(service == null) return;

            service.unignoreInboxItems(inboxItem.getThingUID()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
            inboxItem.setFlag(OHInboxItem.FLAG_NEW);
        }

        public void sendCommand(final String item, final String command){
            OpenHabService service = getService();
            if(service == null) return;

            service.sendCommand(command, item).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.d(TAG, "Sent sendCommand " + command);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable e) {
                    Log.e(TAG, "Error: sending command " + server.getLocalUrl() + " body: " + command, e);
                }
            });
        }

        public void requestSitemaps(final OHCallback<List<OHSitemap>> sitemapsCallback){
            OpenHabService service = getService();
            if(service == null) {
                Log.d(TAG, "Failed to request sitemap, service is null");
                sitemapsCallback.onError();
                return;
            }

            service.listSitemaps().enqueue(new Callback<List<OHSitemap>>() {
                @Override
                public void onResponse(Call<List<OHSitemap>> call, Response<List<OHSitemap>> response) {
                    OHResponse<List<OHSitemap>> sitemapResponse = new OHResponse.Builder<>(response.body())
                            .fromCache(false)
                            .build();
                    sitemapsCallback.onUpdate(sitemapResponse);
                }

                @Override
                public void onFailure(Call<List<OHSitemap>> call, Throwable t) {
                    sitemapsCallback.onError();
                }
            });
        }

        /**
         * Request sitemaps from servera
         * @return observer for remote sitemaps
         */
        public Observable<List<OHSitemap>> requestSitemapObservable(){
             return getService().listSitemapsRx();
        }
    }
}
