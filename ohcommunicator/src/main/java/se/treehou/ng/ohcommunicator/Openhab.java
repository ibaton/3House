package se.treehou.ng.ohcommunicator;

import android.content.Context;

import java.util.List;
import java.util.Map;

import se.treehou.ng.ohcommunicator.core.OHServerWrapper;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.Scanner;
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback;

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
        Map<Long, Connector.ServerHandler> serverHandlers = instance.connector.getServerHandlers();
        for(Connector.ServerHandler handler : serverHandlers.values()){
            handler.stop();
        }
    }

    public static Connector.ServerHandler instance(OHServerWrapper server){
        return instance(server.getId());
    }

    public static Connector.ServerHandler instance(long serverId){
        return instance.connector.getServerHandler(serverId);
    }

    public static void registerServerDiscoveryListener(OHCallback<List<OHServerWrapper>> listener){
        instance.scanner.registerServerDiscoveryListener(listener);
    }

    public static void deregisterServerDiscoveryListener(OHCallback<List<OHServerWrapper>> listener){
        instance.scanner.deregisterServerDiscoveryListener(listener);
    }
}
