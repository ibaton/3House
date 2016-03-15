package se.treehou.ng.ohcommunicator.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.treehou.ng.ohcommunicator.connector.BasicAuthServiceGenerator;
import se.treehou.ng.ohcommunicator.connector.ConnectorUtil;
import se.treehou.ng.ohcommunicator.connector.Constants;
import se.treehou.ng.ohcommunicator.connector.GsonHelper;
import se.treehou.ng.ohcommunicator.connector.OpenHabService;
import se.treehou.ng.ohcommunicator.connector.TrustModifier;
import se.treehou.ng.ohcommunicator.core.OHBindingWrapper;
import se.treehou.ng.ohcommunicator.core.OHInboxItemWrapper;
import se.treehou.ng.ohcommunicator.core.OHItemWrapper;
import se.treehou.ng.ohcommunicator.core.OHServerWrapper;
import se.treehou.ng.ohcommunicator.core.OHSitemapWrapper;
import se.treehou.ng.ohcommunicator.core.db.OHserver;
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback;
import se.treehou.ng.ohcommunicator.services.callbacks.OHResponse;
import se.treehou.ng.ohcommunicator.util.ThreadPool;

public class Connector {

    private static final String TAG = Connector.class.getSimpleName();

    private static final int UPDATE_FREQUENCY = 5000;

    private Context context;
    private Map<Long, ServerHandler> serverHandlers = new HashMap<>();

    public Connector(Context context) {
        this.context = context;
    }

    public static OpenHabService generateOpenHabService(OHServerWrapper server, String url){
        try {
            return BasicAuthServiceGenerator.createService(OpenHabService.class, server, url);
        }
        catch (Exception e) {
            Log.e(TAG, "Failed to create OpenhabService ", e);
        }
        return null;
    }

    public ServerHandler getServerHandler(long serverId){
        ServerHandler serverHandler = serverHandlers.get(serverId);
        if(serverHandler == null) {
            serverHandler = new ServerHandler(serverId, context);
            serverHandler.start();
        }
        serverHandlers.put(serverId, serverHandler);
        return serverHandler;
    }

    public Map<Long, ServerHandler> getServerHandlers() {
        return serverHandlers;
    }

    public static class ServerHandler {

        private long serverId;
        private Timer scheduler;
        private Context context;

        private OpenHabService openHabService;

        private List<OHInboxItemWrapper> inboxItems = new ArrayList<>();
        private List<OHCallback<List<OHInboxItemWrapper>>> inboxCallbacks = new ArrayList<>();

        private List<OHBindingWrapper> bindings = new ArrayList<>();
        private List<OHCallback<List<OHBindingWrapper>>> bindingCallbacks = new ArrayList<>();

        private Map<String, List<OHCallback<OHItemWrapper>>> itemCallbacks = new HashMap<>();


        private List<OHItemWrapper> items = new ArrayList<>();
        private List<OHCallback<List<OHItemWrapper>>> itemsCallbacks = new ArrayList<>();

        private List<OHCallback<List<OHSitemapWrapper>>> sitemapCallbacks = new ArrayList<>();

        public ServerHandler(long serverId, Context context) {
            this.serverId = serverId;
            this.context = context;

            OHServerWrapper server = new OHServerWrapper(OHserver.load(serverId));
            openHabService = generateOpenHabService(server, server.getUrl());
        }

        public void registerBindingListener(OHCallback<List<OHBindingWrapper>> bindingCallback){
            if(bindingCallback == null){
                return;
            }
            bindingCallbacks.add(bindingCallback);
            bindingCallback.onUpdate(new OHResponse.Builder<List<OHBindingWrapper>>(new ArrayList<>(bindings)).fromCache(true).build());
        }

        public void deregisterBindingListener(OHCallback<List<OHBindingWrapper>> binidngCallback){
            bindingCallbacks.remove(binidngCallback);
        }

        public void registerInboxListener(final OHCallback<List<OHInboxItemWrapper>> inboxCallback){
            if(inboxCallback == null) return;

            inboxCallbacks.add(inboxCallback);
            inboxCallback.onUpdate(new OHResponse.Builder<List<OHInboxItemWrapper>>(new ArrayList<>(inboxItems)).fromCache(true).build());

            Uri uri = Uri.parse(getUrl()).buildUpon().appendPath(Constants.PATH_REST).appendPath(Constants.PATH_INBOX).build();
            connectServer(uri, new TypeToken<List<OHInboxItemWrapper>>(){}.getType(), inboxCallback);
        }

        public void deregisterInboxListener(OHCallback<List<OHInboxItemWrapper>> inboxCallback){
            bindingCallbacks.remove(inboxCallback);
        }

        /**
         * Grab item by name from list of items.
         *
         * @param name the name of item.
         * @return the item if found, else null.
         */
        private OHItemWrapper getItem(String name){

            for(OHItemWrapper item : items){
                if(item.getName().equals(name)){
                    return item;
                }
            }
            return null;
        }

        public void registerItemListener(String itemName, OHCallback<OHItemWrapper> itemCallback){
            if(itemCallback == null){
                return;
            }

            List<OHCallback<OHItemWrapper>> callbacks = itemCallbacks.get(itemName);
            if(callbacks == null){
                callbacks = new ArrayList<>();
                itemCallbacks.put(itemName, callbacks);
            }

            callbacks.add(itemCallback);

            // TODO Trigger request
            OHItemWrapper item = getItem(itemName);
            if(item != null) {
                itemCallback.onUpdate(new OHResponse.Builder<>(item).fromCache(true).build());
            }
        }

        public void deregisterItemListener(OHCallback<OHItemWrapper> inboxCallback){
            bindingCallbacks.remove(inboxCallback);
        }

        public void registerItemsListener(OHCallback<List<OHItemWrapper>> itemCallback){
            if(itemCallback == null){
                return;
            }
            itemsCallbacks.add(itemCallback);
            itemCallback.onUpdate(new OHResponse.Builder<List<OHItemWrapper>>(new ArrayList<>(items)).fromCache(true).build());
        }

        public void deregisterItemsListener(OHCallback<List<OHItemWrapper>> itemCallback){
            itemsCallbacks.remove(itemCallback);
        }

        /**
         * Get url from server.
         *
         * @return url for server.
         */
        private String getUrl(){
            // TODO determine if local or remote
            OHServerWrapper server = OHServerWrapper.load(serverId);
            String url = server.getLocalUrl();
            NetworkInfo networkInfo = getNetworkInfo(context);
            if(networkInfo == null || !networkInfo.isConnected()){
                return null;
            }
            if(!isConnectedWifi(context)){
                url = server.getRemoteUrl();
            }

            return url;
        }

        private OpenHabService getService(){
            OHServerWrapper server = OHServerWrapper.load(serverId);
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
        public void approveInboxItem (OHInboxItemWrapper inboxItem){
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
            bindings.remove(inboxItem);
            updateInboxItems(inboxItems);
        }

        /**
         * Send an ignore request to inbox.
         * Update all listeners.
         *
         * @param inboxItem the inbox item to ignore.
         */
        public void ignoreInboxItem(OHInboxItemWrapper inboxItem){
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
            inboxItem.setFlag(OHInboxItemWrapper.FLAG_IGNORED);
            updateInboxItems(inboxItems);
        }

        /**
         * Send an unignore request to inbox.
         * Update all listeners.
         *
         * @param inboxItem the inbox item to unignore.
         */
        public void unignoreInboxItem(OHInboxItemWrapper inboxItem){
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
            inboxItem.setFlag(OHInboxItemWrapper.FLAG_NEW);
            updateInboxItems(inboxItems);
        }

        /**
         * Get all inbox items.
         */
        public List<OHInboxItemWrapper> getInboxItems(){
            return new ArrayList<>(inboxItems);
        }

        /**
         * Update all listeners {@link OHCallback<List< OHInboxItemWrapper >>} with provided items.
         *
         * @param items updateInboxItems all listeners.
         */
        private void updateInboxItems(List<OHInboxItemWrapper> items){
            if(items == null){
                items = new ArrayList<>();
            }

            inboxItems = new ArrayList<>(items);
            for(OHCallback<List<OHInboxItemWrapper>> callback : inboxCallbacks){
                callback.onUpdate(new OHResponse.Builder<List<OHInboxItemWrapper>>(new ArrayList<>(items)).fromCache(false).build());
            }
        }

        /**
         * Update all listeners {@link OHCallback<List< OHItemWrapper >>} with provided items.
         *
         * @param newItems updateItems all listeners.
         */
        private void updateItems(List<OHItemWrapper> newItems){
            if(items == null){
                items = new ArrayList<>();
            }

            items = new ArrayList<>(items);
            for(OHCallback<List<OHItemWrapper>> callback : itemsCallbacks){
                callback.onUpdate(new OHResponse.Builder<List<OHItemWrapper>>(new ArrayList<>(items)).fromCache(false).build());
            }
        }

        /**
         * Update all listeners {@link OHCallback< OHItemWrapper >} with provided items.
         *
         * @param newItem updateItem all listeners.
         */
        private void updateItem(OHItemWrapper newItem){
            List<OHCallback<OHItemWrapper>> callbacks = itemCallbacks.get(newItem.getName());
            if(callbacks != null){
                for(OHCallback<OHItemWrapper> callback : callbacks){
                    callback.onUpdate(new OHResponse.Builder<>(newItem).fromCache(false).build());
                }
            }
        }

        /**
         * Update all listeners {@link OHCallback<List< OHInboxItemWrapper >>} with provided items.
         *
         * @param items update Bindings for all listeners.
         */
        private void updateBindings(List<OHBindingWrapper> items){
            if(items == null){
                items = new ArrayList<>();
            }

            bindings = new ArrayList<>(items);
            for(OHCallback<List<OHBindingWrapper>> callback : bindingCallbacks){
                callback.onUpdate(new OHResponse.Builder<List<OHBindingWrapper>>(new ArrayList<>(bindings)).fromCache(false).build());
            }
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
                    OHServerWrapper server = OHServerWrapper.load(serverId);
                    Log.e(TAG, "Error: sending command " + server.getUrl() + " body: " + command, e);
                }
            });
        }

        /**
         * Create a realm for for server.
         *
         * @param server the server to connect to
         * @return Realm for server
         */
        private Realm createRealm(OHServerWrapper server){
            Realm realm = new Realm.RealmBuilder()
                    .setPrincipal(server.getUsername())
                    .setPassword(server.getPassword())
                    .setUsePreemptiveAuth(true)
                    .setScheme(Realm.AuthScheme.BASIC)
                    .build();

            return realm;
        }

        public void registerSitemapsListener(/*final OHCallback<RealmResults<OHSitemap>> sitemapsCallback*/){

            /*Uri uri = Uri.parse(getUrl()).buildUpon().appendPath(Constants.PATH_REST).appendPath(Constants.PATH_SITEMAPS).build();
            connectServer(uri, new TypeToken<List<OHSitemap>>() {}.getType(), new OHCallback<List<OHSitemap>>(){
                @Override
                public void onUpdate(OHResponse<List<OHSitemap>> items) {

                    io.realm.Realm realm = OHRealm.realm();
                    if(!realm.isInTransaction()) {
                        realm.beginTransaction();
                    }

                    for(OHSitemap newSitemap : items.body()){
                        OHserver serverDb = OHserver.load(serverId);
                        OHSitemap sitemapDb = io.realm.Realm.getDefaultInstance().where(OHSitemap.class).equalTo("name", newSitemap.getName()).equalTo("server.id", serverId).findFirst();
                        if(sitemapDb == null){
                            newSitemap.setId(OHSitemap.getUniqueId());
                            newSitemap.setServer(serverDb);
                        } else {
                            newSitemap.setId(sitemapDb.getId());
                            newSitemap.setServer(serverDb);
                        }
                        realm.copyToRealmOrUpdate(newSitemap);
                    }
                    realm.commitTransaction();
                    realm.close();

                    sitemapsCallback.onUpdate(new OHResponse.Builder<>(realm.where(OHSitemap.class).findAll()).build());
                }

                @Override
                public void onError() {
                    Log.d(TAG, "registerSitemapsListener error");
                }
            });*/
        }

        private <G> void connectServer(final Uri url, final Type type, final OHCallback<G> callback){

            Log.d(TAG, "Longpolling connection to url " + url);

            ThreadPool.instance().submit(new Runnable() {
                @Override
                public void run() {

                    OHServerWrapper server = OHServerWrapper.load(serverId);

                    Realm realm = null;
                    if(server.requiresAuth()){
                        realm = createRealm(server);
                    }

                    AsyncHttpClient asyncHttpClient = new AsyncHttpClient(
                            new AsyncHttpClientConfig.Builder().setAcceptAnyCertificate(true)
                                    .setHostnameVerifier(new TrustModifier.NullHostNameVerifier())
                                    .setRealm(realm)
                                    .build()
                    );

                    Client client = ClientFactory.getDefault().newClient();
                    OptionsBuilder optBuilder = client.newOptionsBuilder().runtime(asyncHttpClient);

                    UUID atmosphereId = UUID.randomUUID();

                    RequestBuilder request = client.newRequestBuilder()
                            .method(org.atmosphere.wasync.Request.METHOD.GET)
                            .uri(url.toString())
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
                            .decoder(new Decoder<String, G>() {

                                @Override
                                public G decode(Event e, String s) {
                                    if(Event.MESSAGE == e) {
                                        Gson gson = GsonHelper.createRealmGsonBuilder();
                                        G item = gson.fromJson(s, type);

                                        Log.d(TAG, "wasync requestBuilder Updating callback " + item);
                                        callback.onUpdate(new OHResponse.Builder<>(item).fromCache(false).build());
                                        Log.d(TAG, "wasync requestBuilder Updated callback " + callback);
                                        return item;
                                    }
                                    return null;
                                }
                            })
                            .transport(Request.TRANSPORT.LONG_POLLING);                    // Fallback to Long-Polling

                    if (server.requiresAuth()){
                        request.header(Constants.HEADER_AUTHENTICATION, ConnectorUtil.createAuthValue(server.getUsername(), server.getPassword()));
                    }

                    Socket pollSocket = client.create(optBuilder.build());
                    try {
                        Log.d(TAG, "wasync Socket " + pollSocket + " " + request.uri());
                        pollSocket.on(new Function<G>() {

                            @Override
                            public void on(G items) {
                                Log.d(TAG, "wasync Socket received");
                                callback.onUpdate(new OHResponse.Builder<>(items).fromCache(false).build());
                            }
                        }).open(request.build());
                    } catch (IOException | ExceptionInInitializerError e) {
                        Log.d(TAG, "wasync Got error " + e);
                    }

                    Log.d(TAG,"Longpolling Poller started");
                }
            });
        }

        private void start(){
            if(scheduler == null) {
                scheduler = new Timer();
                scheduler.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        OpenHabService service = getService();

                        if(service == null) {
                            return;
                        }

                        /*if (inboxCallbacks.size() > 0) {
                            Log.d(TAG, "Requesting inbox updateInboxItems");
                            service.listInboxItems().enqueue(new Callback<List<OHInboxItemWrapper>>() {

                                @Override
                                public void onResponse(Call<List<OHInboxItemWrapper>> call, Response<List<OHInboxItemWrapper>> response) {
                                    Log.d(TAG, "Inbox updated size " + response.body().size());
                                    updateInboxItems(response.body());
                                }

                                @Override
                                public void onFailure(Call<List<OHInboxItemWrapper>> call, Throwable e) {
                                    Log.e(TAG, "Error requesting inbox", e);
                                }
                            });
                        }*/

                        if (bindingCallbacks.size() > 0) {
                            Log.d(TAG, "Requesting inbox updateInboxItems");
                            service.listBindings().enqueue(new Callback<List<OHBindingWrapper>>() {

                                @Override
                                public void onResponse(Call<List<OHBindingWrapper>> call, Response<List<OHBindingWrapper>> response) {
                                    Log.d(TAG, "Inbox updated size " + response.body().size());
                                    updateBindings(response.body());
                                }

                                @Override
                                public void onFailure(Call<List<OHBindingWrapper>> call, Throwable e) {
                                    Log.e(TAG, "Error requesting bindings", e);
                                }
                            });
                        }

                        if (itemsCallbacks.size() > 0) {
                            Log.d(TAG, "Requesting items");
                            service.listItems().enqueue(new Callback<List<OHItemWrapper>>() {

                                @Override
                                public void onResponse(Call<List<OHItemWrapper>> call, Response<List<OHItemWrapper>> response) {
                                    Log.d(TAG, "Items updated size " + response.body().size());
                                    updateItems(response.body());
                                }

                                @Override
                                public void onFailure(Call<List<OHItemWrapper>> call, Throwable e) {
                                    Log.e(TAG, "Error requesting items", e);
                                }
                            });
                        }

                        if (itemCallbacks.size() > 0) {
                            Log.d(TAG, "Requesting items");
                            for (Map.Entry<String, List<OHCallback<OHItemWrapper>>> entry : itemCallbacks.entrySet()) {
                                service.getItem(entry.getKey()).enqueue(new Callback<OHItemWrapper>() {

                                    @Override
                                    public void onResponse(Call<OHItemWrapper> call, Response<OHItemWrapper> response) {
                                        Log.d(TAG, "Item updated size " + response.body());
                                        OHItemWrapper item = response.body();
                                        updateItem(item);
                                    }

                                    @Override
                                    public void onFailure(Call<OHItemWrapper> call, Throwable e) {
                                        Log.e(TAG, "Error requesting items", e);
                                    }
                                });
                            }
                        }
                    }
                }, 0, UPDATE_FREQUENCY);
            }
        }

        /**
         * Stop the server handler.
         */
        public void stop(){
            scheduler.cancel();
        }
    }
}
