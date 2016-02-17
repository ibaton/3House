package se.treehou.ng.ohcommunicator.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.treehou.ng.ohcommunicator.connector.BasicAuthServiceGenerator;
import se.treehou.ng.ohcommunicator.connector.OpenHabService;
import se.treehou.ng.ohcommunicator.core.OHBinding;
import se.treehou.ng.ohcommunicator.core.OHInboxItem;
import se.treehou.ng.ohcommunicator.core.OHServer;
import se.treehou.ng.ohcommunicator.services.callbacks.Callback1;

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
        private List<Callback1<List<OHInboxItem>>> inboxCallbacks = new ArrayList<>();

        private List<OHBinding> bindings = new ArrayList<>();
        private List<Callback1<List<OHBinding>>> bindingCallbacks = new ArrayList<>();

        public ServerHandler(OHServer server, Context context) {
            this.server = server;
            this.context = context;
            openHabService = generateOpenHabService(server, server.getUrl());
        }

        public void registerBindingListener(Callback1<List<OHBinding>> bindingCallback){
            if(bindingCallback == null){
                return;
            }
            bindingCallbacks.add(bindingCallback);
            bindingCallback.onUpdate(new ArrayList<>(bindings));
        }

        public void deregisterBindingListener(Callback1<List<OHBinding>> binidngCallback){
            bindingCallbacks.remove(binidngCallback);
            if(bindingCallbacks.size() <= 0){
                scheduler.cancel();
            }
        }

        public void registerInboxListener(Callback1<List<OHInboxItem>> inboxCallback){
            if(inboxCallback == null){
                return;
            }
            inboxCallbacks.add(inboxCallback);
            inboxCallback.onUpdate(new ArrayList<>(inboxItems));
        }

        public void deregisterInboxListener(Callback1<List<OHInboxItem>> inboxCallback){
            bindingCallbacks.remove(inboxCallback);
            if(inboxCallbacks.size() <= 0){
                scheduler.cancel();
            }
        }

        private OpenHabService getService(){
            // TODO determine if local or remote

            String url = server.getLocalUrl();
            NetworkInfo networkInfo = getNetworkInfo(context);
            if(networkInfo == null || !networkInfo.isConnected()){
                return null;
            }
            if(!isConnectedWifi(context)){
                url = server.getRemoteUrl();
            }

            openHabService = generateOpenHabService(server, url);

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
         * Update all listeners {@link Callback1<List<OHInboxItem>>} with provided items.
         *
         * @param items updateInboxItems all listeners.
         */
        private void updateInboxItems(List<OHInboxItem> items){
            if(items == null){
                items = new ArrayList<>();
            }

            inboxItems = new ArrayList<>(items);
            for(Callback1<List<OHInboxItem>> callback : inboxCallbacks){
                callback.onUpdate(new ArrayList<>(items));
            }
        }

        /**
         * Update all listeners {@link Callback1<List<OHInboxItem>>} with provided items.
         *
         * @param items update Bindings for all listeners.
         */
        private void updateBindings(List<OHBinding> items){
            if(items == null){
                items = new ArrayList<>();
            }

            bindings = new ArrayList<>(items);
            for(Callback1<List<OHBinding>> callback : bindingCallbacks){
                callback.onUpdate(new ArrayList<>(bindings));
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

                        if (inboxCallbacks.size() > 0) {
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
                        }

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
