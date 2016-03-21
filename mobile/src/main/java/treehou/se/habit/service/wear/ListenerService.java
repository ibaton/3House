package treehou.se.habit.service.wear;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;

import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.connector.GsonHelper;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import treehou.se.habit.connector.Constants;
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

        Gson gson = GsonHelper.createGsonBuilder();
        VoiceCommandMessage message = gson.fromJson(jMessage, VoiceCommandMessage.class);

        OHServer server = null;
        if(message.haveServer()){
            // TODO server = OHServer.load(message.getServer());
        }else {
            /* TODO List<OHServer> servers = OHServer.loadAll();
            if(servers.size() > 0) {
                server = servers.get(0);
            }*/
        }

        if(server != null) {
            Openhab.instance(server).sendCommand(Constants.ITEM_VOICE_COMMAND, message.getMessage());
        }
    }

}
