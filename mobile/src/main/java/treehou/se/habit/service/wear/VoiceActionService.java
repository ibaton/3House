package treehou.se.habit.service.wear;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.util.Log;

import javax.inject.Inject;

import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.connector.Constants;
import treehou.se.habit.util.ConnectionFactory;

public class VoiceActionService extends IntentService {

    private static final String TAG = "VoiceActionService";

    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";

    @Inject ConnectionFactory connectionFactory;

    public VoiceActionService() {
        super("VoiceActionService");

        ((HabitApplication) getApplication()).component().inject(this);
        Log.d(TAG, "Constructor");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String command = getMessageText(intent);
        if(command != null){
            Log.d(TAG, "Received sendCommand " + intent);

            OHServer server = null;
            // TODO server
            /*List<OHServer> servers = OHServer.loadAll();
            if(servers.size() > 0) {
                server = servers.get(0);
            }*/

            if(server != null) {
                IServerHandler serverHandler = connectionFactory.createServerHandler(server, this);
                serverHandler.sendCommand(Constants.ITEM_VOICE_COMMAND, command);
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
