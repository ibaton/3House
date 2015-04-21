package treehou.se.habit.service.wear;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;

import java.util.List;

import treehou.se.habit.connector.Communicator;
import treehou.se.habit.connector.Constants;
import treehou.se.habit.core.Server;
import treehou.se.habit.core.Util;
import treehou.se.habit.service.wear.connector.messages.VoiceCommandMessage;

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

        Server server = null;
        if(message.haveServer()){
            server = Server.load(Server.class, message.getServer());
        }else {
            List<Server> servers = Server.getServers();
            if(servers.size() > 0) {
                server = servers.get(0);
            }
        }

        if(server != null) {
            Communicator.instance(this).command(server, Constants.ITEM_VOICE_COMMAND, message.getMessage());
        }
    }

}
