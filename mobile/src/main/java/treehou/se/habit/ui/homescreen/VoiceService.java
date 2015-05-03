package treehou.se.habit.ui.homescreen;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;

import java.util.List;

import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.Server;

public class VoiceService extends IntentService {

    public static final String TAG = "VoiceService";

    private static final String ACTION_COMMAND = "treehou.se.habit.ui.homescreen.action.VOICE";
    public static final String EXTRA_SERVER = "extraServerId";

    public static final String VOICE_ITEM = "VoiceCommand";

    private static final long NULL_SERVER = -1;

    public static Intent createVoiceCommand(Context context, Server server) {
        Intent intent = new Intent(context, VoiceService.class);
        intent.setAction(ACTION_COMMAND);
        intent.putExtra(EXTRA_SERVER, server.getId());
        return intent;
    }

    public VoiceService() {
        super("VoiceService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.w(TAG, "onHandleIntent.");

        long serverId = intent.getLongExtra(EXTRA_SERVER, NULL_SERVER);
        if(NULL_SERVER == serverId){
            Log.w(TAG, "No server specified.");
            return;
        }
        Server server = Server.load(Server.class, serverId);

        List<String> results = intent.getExtras().getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
        if (!results.isEmpty()) {
            Log.d(TAG, "Received " + results.size() + " voice results.");

            String command = results.get(0);
            Communicator communicator = Communicator.instance(this);
            communicator.command(server, VOICE_ITEM, command);
        }
    }
}
