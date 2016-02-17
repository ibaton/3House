package treehou.se.habit.service.wear;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.util.Log;

import java.util.List;

import se.treehou.ng.ohcommunicator.Openhab;
import treehou.se.habit.connector.Constants;
import treehou.se.habit.core.db.ServerDB;

public class VoiceActionService extends IntentService {

    private static final String TAG = "VoiceActionService";

    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";

    public VoiceActionService() {
        super("VoiceActionService");

        Log.d(TAG, "Constructor");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String command = getMessageText(intent);
        if(command != null){
            Log.d(TAG, "Received sendCommand " + intent);

            ServerDB server = null;
            List<ServerDB> servers = ServerDB.getServers();
            if(servers.size() > 0) {
                server = servers.get(0);
            }

            if(server != null) {
                Openhab.instance(ServerDB.toGeneric(server)).sendCommand(Constants.ITEM_VOICE_COMMAND, command);
            }
        }
    }

    private String getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(EXTRA_VOICE_REPLY).toString();
        }
        return null;
    }
}
