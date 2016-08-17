package treehou.se.habit.ui.homescreen;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;

import java.util.List;

import io.realm.Realm;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.core.db.model.ServerDB;

public class VoiceService extends IntentService {

    public static final String TAG = "VoiceService";

    private static final String ACTION_COMMAND = "treehou.se.habit.ui.homescreen.action.VOICE";
    public static final String EXTRA_SERVER = "extraServerId";

    public static final String VOICE_ITEM = "VoiceCommand";

    private static final int NULL_SERVER = -1;

    public static Intent createVoiceCommand(Context context, ServerDB server) {
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

        Realm realm = Realm.getDefaultInstance();
        ServerDB server = ServerDB.load(realm, serverId);

        List<String> results = intent.getExtras().getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
        if (results != null && !results.isEmpty() && server != null) {
            Log.d(TAG, "Received " + results.size() + " voice results.");

            String command = results.get(0);
            IServerHandler serverHandler = new Connector.ServerHandler(server.toGeneric(), this);
            serverHandler.sendCommand(VOICE_ITEM, command);
        }
        realm.close();
    }
}
