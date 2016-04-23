package se.treehou.ng.ohcommunicator;

import android.content.Context;

import java.util.List;

import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.Scanner;
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback;

public class Openhab {

    private static Openhab instance;

    private Scanner scanner;
    private Connector connector;

    private Openhab(Context context){
        scanner = new Scanner(context);
        connector = new Connector(context);
    }

    public static void setup(Context context){
        instance = new Openhab(context);
    }

    public static Connector.ServerHandler instance(OHServer server){
        return instance.connector.getServerHandler(server);
    }

    public static void registerServerDiscoveryListener(OHCallback<List<OHServer>> listener){
        instance.scanner.registerServerDiscoveryListener(listener);
    }

    public static void deregisterServerDiscoveryListener(OHCallback<List<OHServer>> listener){
        instance.scanner.deregisterServerDiscoveryListener(listener);
    }
}
