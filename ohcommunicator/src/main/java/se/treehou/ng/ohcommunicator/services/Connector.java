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

        private List<OHInboxItem> lastInboxItems = new ArrayList<>();
        private List<Callback1<List<OHInboxItem>>> callbacks = new ArrayList<>();

        public ServerHandler(OHServer server, Context context) {
            this.server = server;
            this.context = context;
            openHabService = generateOpenHabService(server, server.getUrl());
        }

        public void addInboxListener(Callback1<List<OHInboxItem>> inboxCallback){
            if(inboxCallback == null){
                return;
            }
            callbacks.add(inboxCallback);
            inboxCallback.onUpdate(new ArrayList<>(lastInboxItems));
        }

        public void removeInboxListener(Callback1<List<OHInboxItem>> inboxCallback){
            callbacks.remove(inboxCallback);
            if(callbacks.size() <= 0){
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
            lastInboxItems.remove(inboxItem);
            update(lastInboxItems);
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
            update(lastInboxItems);
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
            update(lastInboxItems);
        }

        /**
         * Get all inbox items.
         */
        public List<OHInboxItem> getInboxItems(){
            return new ArrayList<>(lastInboxItems);
        }

        /**
         * Update all listeners {@link Callback1<List<OHInboxItem>>} with provided items.
         *
         * @param items update all listeners.
         */
        private void update(List<OHInboxItem> items){
            if(items == null){
                items = new ArrayList<>();
            }

            lastInboxItems = new ArrayList<>(items);
            for(Callback1<List<OHInboxItem>> callback : callbacks){
                callback.onUpdate(new ArrayList<>(items));
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
         * Get the number of listeners added to service handler.
         *
         * @return number of listeners.
         */
        public int getListenerCount(){
            return callbacks.size();
        }

        private void start(){
            if(scheduler == null) {
                scheduler = new Timer();
                scheduler.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        OpenHabService service = getService();

                        if(service == null || getListenerCount() <= 0) {
                            Log.d(TAG, "Requesting inbox paused, listeners: " + getListenerCount() + " " + service);
                            return;
                        }

                        Log.d(TAG, "Requesting inbox update");
                        service.listInboxItems().enqueue(new Callback<List<OHInboxItem>>() {

                            @Override
                            public void onResponse(Call<List<OHInboxItem>> call, Response<List<OHInboxItem>> response) {
                                Log.d(TAG, "Inbox updated size " + response.body().size());
                                update(response.body());
                            }

                            @Override
                            public void onFailure(Call<List<OHInboxItem>> call, Throwable e) {
                                Log.e(TAG, "Error requesting inbox", e);
                            }
                        });
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
