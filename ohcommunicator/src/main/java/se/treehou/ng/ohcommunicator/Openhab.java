package se.treehou.ng.ohcommunicator;

import android.content.Context;

import java.util.List;
import java.util.Map;

import se.treehou.ng.ohcommunicator.core.OHInboxItem;
import se.treehou.ng.ohcommunicator.core.OHServer;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.Scanner;
import se.treehou.ng.ohcommunicator.services.callbacks.Callback1;

public class Openhab {

    private static Openhab instance;

    private Context context;
    private Scanner scanner;
    private Connector connector;

    private Openhab(Context context){
        this.context = context;
        scanner = new Scanner(context);
        connector = new Connector(context);
    }

    public static void setup(Context context){
        instance = new Openhab(context);
    }

    public static void stop() {
        Map<OHServer, Connector.ServerHandler> serverHandlers = instance.connector.getServerHandlers();
        for(Connector.ServerHandler handler : serverHandlers.values()){
            handler.stop();
        }
    }

    public static void registerServerDiscoveryListener(Callback1<List<OHServer>> listener){
        instance.scanner.registerServerDiscoveryListener(listener);
    }

    public static void deregisterServerDiscoveryListener(Callback1<List<OHServer>> listener){
        instance.scanner.deregisterServerDiscoveryListener(listener);
    }

    public static void registerInboxListener(OHServer server, Callback1<List<OHInboxItem>> listener) {
        instance.connector.getServerHandler(server).addInboxListener(listener);
    }

    public static void deregisterInboxListener(OHServer server, Callback1<List<OHInboxItem>> listener) {
        instance.connector.getServerHandler(server).removeInboxListener(listener);
    }

    public static void approveInboxItem(OHServer server, OHInboxItem inboxItem){
        instance.connector.getServerHandler(server).approveInboxItem(inboxItem);
    }

    public static void ignoreInboxItem(OHServer server, OHInboxItem inboxItem){
        instance.connector.getServerHandler(server).ignoreInboxItem(inboxItem);
    }

    public static void unignoreInboxItem(OHServer server, OHInboxItem inboxItem){
        instance.connector.getServerHandler(server).unignoreInboxItem(inboxItem);
    }

    public static List<OHInboxItem> getInboxItems(OHServer server){
        return instance.connector.getServerHandler(server).getInboxItems();
    }
}
