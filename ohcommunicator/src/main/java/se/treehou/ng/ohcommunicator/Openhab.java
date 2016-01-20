package se.treehou.ng.ohcommunicator;

import android.content.Context;

import java.util.List;

import se.treehou.ng.ohcommunicator.core.OHServer;
import se.treehou.ng.ohcommunicator.services.OHScanner;
import se.treehou.ng.ohcommunicator.services.callbacks.Callback1;

public class Openhab {

    private static Openhab instance;

    private Context context;
    private OHScanner scanner;

    private Openhab(Context context){
        this.context = context;
        scanner = new OHScanner(context);
    }

    public static void setup(Context context){
        instance = new Openhab(context);
    }

    public static void registerServerDiscoveryListener(Callback1<List<OHServer>> listener){
        instance.scanner.registerServerDiscoveryListener(listener);
    }

    public static void deregisterServerDiscoveryListener(Callback1<List<OHServer>> listener){
        instance.scanner.deregisterServerDiscoveryListener(listener);
    }
}
