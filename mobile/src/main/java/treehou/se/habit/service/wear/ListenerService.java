package treehou.se.habit.service.wear;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;

import java.util.List;

import treehou.se.habit.connector.Communicator;
import treehou.se.habit.connector.Constants;
import treehou.se.habit.core.db.ServerDB;
import treehou.se.habit.service.wear.connector.messages.VoiceCommandMessage;
import treehou.se.habit.util.Util;

public class ListenerService extends WearableListenerService {

    private static final String TAG = "ListenerService";

    public static final String PATH_VOICE_COMMAND = "/voice/command";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if(PATH_VOICE_COMMAND.equals(messageEvent.getPath())) {
            sendCommand(new String(messageEvent.getData()));
        }
    }

    private void sendCommand(String jMessage) {

        Log.d(TAG, "Recieved voice command");

        Gson gson = Util.createGsonBuilder();
        VoiceCommandMessage message = gson.fromJson(jMessage, VoiceCommandMessage.class);

        ServerDB server = null;
        if(message.haveServer()){
            server = ServerDB.load(ServerDB.class, message.getServer());
        }else {
            List<ServerDB> servers = ServerDB.getServers();
            if(servers.size() > 0) {
                server = servers.get(0);
            }
        }

        if(server != null) {
            Communicator.instance(this).command(server, Constants.ITEM_VOICE_COMMAND, message.getMessage());
        }
    }

}
