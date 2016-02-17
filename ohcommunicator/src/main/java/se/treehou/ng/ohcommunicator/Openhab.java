package se.treehou.ng.ohcommunicator;

import android.content.Context;

import java.util.List;
import java.util.Map;

import se.treehou.ng.ohcommunicator.core.OHBinding;
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

    public static Connector.ServerHandler instance(OHServer server){
        return instance.connector.getServerHandler(server);
    }

    public static void registerServerDiscoveryListener(Callback1<List<OHServer>> listener){
        instance.scanner.registerServerDiscoveryListener(listener);
    }

    public static void deregisterServerDiscoveryListener(Callback1<List<OHServer>> listener){
        instance.scanner.deregisterServerDiscoveryListener(listener);
    }
}
