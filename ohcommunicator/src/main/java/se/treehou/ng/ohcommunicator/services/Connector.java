package se.treehou.ng.ohcommunicator.services;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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

    private Map<OHServer, ServerHandler> serverHandlers = new HashMap<>();

    public Connector(Context context) {}

    public static OpenHabService generateOpenHabService(OHServer server){
        try {
            return BasicAuthServiceGenerator.createService(OpenHabService.class, server);
        }
        catch (Exception e) {
            Log.e(TAG, "Failed to create OpenhabService ", e);
        }
        return null;
    }

    public ServerHandler getServerHandler(OHServer server){
        ServerHandler serverHandler = serverHandlers.get(server);
        if(serverHandler == null) {
            serverHandler = new ServerHandler(server);
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
        private OpenHabService openHabService;
        private Timer scheduler;

        private List<OHInboxItem> lastInboxItems = new ArrayList<>();
        private List<Callback1<List<OHInboxItem>>> callbacks = new ArrayList<>();

        public ServerHandler(OHServer server) {
            this.server = server;
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

        /**
         * Send an approve request to inbox.
         * Update all listeners.
         *
         * @param inboxItem the inbox item to approve.
         */
        public void approveInboxItem (OHInboxItem inboxItem){
            openHabService.approveInboxItems(inboxItem.getThingUID()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Response<Void> response) {}

                @Override
                public void onFailure(Throwable t) {}
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
            openHabService.ignoreInboxItems(inboxItem.getThingUID()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Response<Void> response) {}

                @Override
                public void onFailure(Throwable t) {}
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
            openHabService.unignoreInboxItems(inboxItem.getThingUID()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Response<Void> response) {}

                @Override
                public void onFailure(Throwable t) {}
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

        /**
         * Get the number of listeners added to service handler.
         *
         * @return number of listeners.
         */
        public int getListenerCount(){
            return callbacks.size();
        }

        private void start(){
            openHabService = generateOpenHabService(server);
            if(scheduler == null) {
                scheduler = new Timer();
                scheduler.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if(getListenerCount() <= 0) return;

                        Log.d(TAG, "Requesting inbox update");
                        openHabService.listInboxItems().enqueue(new Callback<List<OHInboxItem>>() {
                            @Override
                            public void onResponse(Response<List<OHInboxItem>> response) {
                                Log.d(TAG, "Inbox updated size " + response.body().size());
                                update(response.body());
                            }

                            @Override
                            public void onFailure(Throwable e) {
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
