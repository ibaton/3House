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
import se.treehou.ng.ohcommunicator.core.OHBinding;
import se.treehou.ng.ohcommunicator.core.OHInboxItem;
import se.treehou.ng.ohcommunicator.core.OHItem;
import se.treehou.ng.ohcommunicator.core.OHServer;
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback;
import se.treehou.ng.ohcommunicator.services.callbacks.OHResponse;
import se.treehou.ng.ohcommunicator.util.ThreadPool;

public class Connector {

    private static final String TAG = Connector.class.getSimpleName();

    private static final int UPDATE_FREQUENCY = 5000;

    private Context context;
    private Map<OHServer, ServerHandler> serverHandlers = new HashMap<>();

    public Connector(Context context) {
        this.context = context;
    }

    public static OpenHabService generateOpenHabService(OHServer server, String url){
        try {
            return BasicAuthServiceGenerator.createService(OpenHabService.class, server, url);
        }
        catch (Exception e) {
            Log.e(TAG, "Failed to create OpenhabService ", e);
        }
        return null;
    }

    public ServerHandler getServerHandler(OHServer server){
        ServerHandler serverHandler = serverHandlers.get(server);
        if(serverHandler == null) {
            serverHandler = new ServerHandler(server, context);
            serverHandler.start();
        }
        serverHandlers.put(server, serverHandler);
        return serverHandler;
    }

    public Map<OHServer, ServerHandler> getServerHandlers() {
        return serverHandlers;
    }

    public static class ServerHandler {

        private OHServer server;
        private Timer scheduler;
        private Context context;

        private OpenHabService openHabService;

        private List<OHInboxItem> inboxItems = new ArrayList<>();
        private List<OHCallback<List<OHInboxItem>>> inboxCallbacks = new ArrayList<>();

        private List<OHBinding> bindings = new ArrayList<>();
        private List<OHCallback<List<OHBinding>>> bindingCallbacks = new ArrayList<>();

        private Map<String, List<OHCallback<OHItem>>> itemCallbacks = new HashMap<>();


        private List<OHItem> items = new ArrayList<>();
        private List<OHCallback<List<OHItem>>> itemsCallbacks = new ArrayList<>();

        public ServerHandler(OHServer server, Context context) {
            this.server = server;
            this.context = context;
            openHabService = generateOpenHabService(server, server.getUrl());
        }

        public void registerBindingListener(OHCallback<List<OHBinding>> bindingCallback){
            if(bindingCallback == null){
                return;
            }
            bindingCallbacks.add(bindingCallback);
            bindingCallback.onUpdate(new OHResponse.Builder<List<OHBinding>>(new ArrayList<>(bindings)).fromCache(true).build());
        }

        public void deregisterBindingListener(OHCallback<List<OHBinding>> binidngCallback){
            bindingCallbacks.remove(binidngCallback);
        }

        public void registerInboxListener(final OHCallback<List<OHInboxItem>> inboxCallback){
            if(inboxCallback == null) return;

            inboxCallbacks.add(inboxCallback);
            inboxCallback.onUpdate(new OHResponse.Builder<List<OHInboxItem>>(new ArrayList<>(inboxItems)).fromCache(true).build());

            Uri uri = Uri.parse(getUrl()).buildUpon().appendPath(Constants.PATH_REST).appendPath(Constants.PATH_INBOX).build();
            connectServer(uri, inboxCallback);
        }

        public void deregisterInboxListener(OHCallback<List<OHInboxItem>> inboxCallback){
            bindingCallbacks.remove(inboxCallback);
        }

        /**
         * Grab item by name from list of items.
         *
         * @param name the name of item.
         * @return the item if found, else null.
         */
        private OHItem getItem(String name){

            for(OHItem item : items){
                if(item.getName().equals(name)){
                    return item;
                }
            }
            return null;
        }

        public void registerItemListener(String itemName, OHCallback<OHItem> itemCallback){
            if(itemCallback == null){
                return;
            }

            List<OHCallback<OHItem>> callbacks = itemCallbacks.get(itemName);
            if(callbacks == null){
                callbacks = new ArrayList<>();
                itemCallbacks.put(itemName, callbacks);
            }

            callbacks.add(itemCallback);

            // TODO Trigger request
            OHItem item = getItem(itemName);
            if(item != null) {
                itemCallback.onUpdate(new OHResponse.Builder<>(item).fromCache(true).build());
            }
        }

        public void deregisterItemListener(OHCallback<OHItem> inboxCallback){
            bindingCallbacks.remove(inboxCallback);
        }

        public void registerItemsListener(OHCallback<List<OHItem>> itemCallback){
            if(itemCallback == null){
                return;
            }
            itemsCallbacks.add(itemCallback);
            itemCallback.onUpdate(new OHResponse.Builder<List<OHItem>>(new ArrayList<>(items)).fromCache(true).build());
        }

        public void deregisterItemsListener(OHCallback<List<OHItem>> itemCallback){
            itemsCallbacks.remove(itemCallback);
        }

        /**
         * Get url from server.
         *
         * @return url for server.
         */
        private String getUrl(){
            // TODO determine if local or remote
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
            bindings.remove(inboxItem);
            updateInboxItems(inboxItems);
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
            updateInboxItems(inboxItems);
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
            updateInboxItems(inboxItems);
        }

        /**
         * Get all inbox items.
         */
        public List<OHInboxItem> getInboxItems(){
            return new ArrayList<>(inboxItems);
        }

        /**
         * Update all listeners {@link OHCallback<List<OHInboxItem>>} with provided items.
         *
         * @param items updateInboxItems all listeners.
         */
        private void updateInboxItems(List<OHInboxItem> items){
            if(items == null){
                items = new ArrayList<>();
            }

            inboxItems = new ArrayList<>(items);
            for(OHCallback<List<OHInboxItem>> callback : inboxCallbacks){
                callback.onUpdate(new OHResponse.Builder<List<OHInboxItem>>(new ArrayList<>(items)).fromCache(false).build());
            }
        }

        /**
         * Update all listeners {@link OHCallback<List<OHItem>>} with provided items.
         *
         * @param newItems updateItems all listeners.
         */
        private void updateItems(List<OHItem> newItems){
            if(items == null){
                items = new ArrayList<>();
            }

            items = new ArrayList<>(items);
            for(OHCallback<List<OHItem>> callback : itemsCallbacks){
                callback.onUpdate(new OHResponse.Builder<List<OHItem>>(new ArrayList<>(items)).fromCache(false).build());
            }
        }

        /**
         * Update all listeners {@link OHCallback<OHItem>} with provided items.
         *
         * @param newItem updateItem all listeners.
         */
        private void updateItem(OHItem newItem){
            List<OHCallback<OHItem>> callbacks = itemCallbacks.get(newItem.getName());
            if(callbacks != null){
                for(OHCallback<OHItem> callback : callbacks){
                    callback.onUpdate(new OHResponse.Builder<>(newItem).fromCache(false).build());
                }
            }
        }

        /**
         * Update all listeners {@link OHCallback<List<OHInboxItem>>} with provided items.
         *
         * @param items update Bindings for all listeners.
         */
        private void updateBindings(List<OHBinding> items){
            if(items == null){
                items = new ArrayList<>();
            }

            bindings = new ArrayList<>(items);
            for(OHCallback<List<OHBinding>> callback : bindingCallbacks){
                callback.onUpdate(new OHResponse.Builder<List<OHBinding>>(new ArrayList<>(bindings)).fromCache(false).build());
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
        private Realm createRealm(OHServer server){
            Realm realm = new Realm.RealmBuilder()
                    .setPrincipal(server.getUsername())
                    .setPassword(server.getPassword())
                    .setUsePreemptiveAuth(true)
                    .setScheme(Realm.AuthScheme.BASIC)
                    .build();

            return realm;
        }

        private <G> void connectServer(final Uri url, final OHCallback<G> callback){

            ThreadPool.instance().submit(new Runnable() {
                @Override
                public void run() {

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
                                        Gson gson = GsonHelper.createGsonBuilder();
                                        G item = gson.fromJson(s, new TypeToken<G>() {}.getType());
                                        callback.onUpdate(new OHResponse.Builder<>(item).fromCache(false).build());

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

                    Log.d(TAG,"wasync Poller started");
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
                            service.listInboxItems().enqueue(new Callback<List<OHInboxItem>>() {

                                @Override
                                public void onResponse(Call<List<OHInboxItem>> call, Response<List<OHInboxItem>> response) {
                                    Log.d(TAG, "Inbox updated size " + response.body().size());
                                    updateInboxItems(response.body());
                                }

                                @Override
                                public void onFailure(Call<List<OHInboxItem>> call, Throwable e) {
                                    Log.e(TAG, "Error requesting inbox", e);
                                }
                            });
                        }*/

                        if (bindingCallbacks.size() > 0) {
                            Log.d(TAG, "Requesting inbox updateInboxItems");
                            service.listBindings().enqueue(new Callback<List<OHBinding>>() {

                                @Override
                                public void onResponse(Call<List<OHBinding>> call, Response<List<OHBinding>> response) {
                                    Log.d(TAG, "Inbox updated size " + response.body().size());
                                    updateBindings(response.body());
                                }

                                @Override
                                public void onFailure(Call<List<OHBinding>> call, Throwable e) {
                                    Log.e(TAG, "Error requesting bindings", e);
                                }
                            });
                        }

                        if (itemsCallbacks.size() > 0) {
                            Log.d(TAG, "Requesting items");
                            service.listItems().enqueue(new Callback<List<OHItem>>() {

                                @Override
                                public void onResponse(Call<List<OHItem>> call, Response<List<OHItem>> response) {
                                    Log.d(TAG, "Items updated size " + response.body().size());
                                    updateItems(response.body());
                                }

                                @Override
                                public void onFailure(Call<List<OHItem>> call, Throwable e) {
                                    Log.e(TAG, "Error requesting items", e);
                                }
                            });
                        }

                        if (itemCallbacks.size() > 0) {
                            Log.d(TAG, "Requesting items");
                            for (Map.Entry<String, List<OHCallback<OHItem>>> entry : itemCallbacks.entrySet()) {
                                service.getItem(entry.getKey()).enqueue(new Callback<OHItem>() {

                                    @Override
                                    public void onResponse(Call<OHItem> call, Response<OHItem> response) {
                                        Log.d(TAG, "Item updated size " + response.body());
                                        OHItem item = response.body();
                                        updateItem(item);
                                    }

                                    @Override
                                    public void onFailure(Call<OHItem> call, Throwable e) {
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
