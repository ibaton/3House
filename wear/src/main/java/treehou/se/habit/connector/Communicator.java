package treehou.se.habit.connector;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

import treehou.se.habit.connector.messages.VoiceCommandMessage;

/**
 * Created by ibaton on 2015-03-12.
 */
public class Communicator {

    private static final String TAG = "WearCommunicator";

    public static final String PATH_VOICE_COMMAND = "/voice/command";

    private long CONNECTION_TIME_OUT_MS = 5000;

    private static Communicator instance;

    private Context context;
    private GoogleApiClient googleApiClient;

    public static synchronized Communicator instance(Context context){
        if (instance == null) {
            instance = new Communicator(context);
        }
        return instance;
    }

    private Communicator(Context context){
        this.context = context;

        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        // Now you can use the Data Layer API
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                }).addApi(Wearable.API)
                .build();
    }

    private String retrieveDeviceNode() {

        googleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
        NodeApi.GetConnectedNodesResult result =
                Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
        List<Node> nodes = result.getNodes();
        String nodeId;
        if (nodes.size() > 0) {
            nodeId = nodes.get(0).getId();
            return nodeId;
        }
        return null;
    }

    public void sendCommand(final String command) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                //TODO handle null
                String nodeId = retrieveDeviceNode();

                Gson gson = new GsonBuilder().create();
                VoiceCommandMessage message = new VoiceCommandMessage(command);

                googleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                Wearable.MessageApi.sendMessage(googleApiClient, nodeId, PATH_VOICE_COMMAND, gson.toJson(message).getBytes());
                googleApiClient.disconnect();
            }
        }).start();
    }
}
