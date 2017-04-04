package treehou.se.habit.ui.control;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import javax.inject.Inject;

import io.realm.Realm;
import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.db.model.ItemDB;
import treehou.se.habit.util.ConnectionFactory;
import treehou.se.habit.util.Util;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class CommandService extends IntentService {

    private static final String TAG = "CommandService";

    private static final String ARG_ITEM = "ARG_ITEM";

    private static final String ACTION_COMMAND = "ACTION_COMMAND";
    private static final String ARG_COMMAND = "ARG_COMMAND";

    private static final String ACTION_INC_DEC = "ACTION_INC_DEC";
    private static final String ARG_MAX     = "ARG_MAX";
    private static final String ARG_MIN     = "ARG_MIN";
    private static final String ARG_VALUE   = "ARG_VALUE";

    @Inject ConnectionFactory connectionFactory;

    public static void startActionCommand(Context context, String command, OHItem item) {
        Intent intent = new Intent(context, CommandService.class);
        intent.setAction(ACTION_COMMAND);
        intent.putExtra(ARG_COMMAND, command);
        intent.putExtra(ARG_ITEM, item.getId());
        context.startService(intent);
    }

    public static Intent getActionCommand(Context context, String command, long itemId) {
        Intent intent = new Intent(context, CommandService.class);
        intent.setAction(ACTION_COMMAND);
        intent.putExtra(ARG_COMMAND, command);
        intent.putExtra(ARG_ITEM, itemId);
        return intent;
    }

    public static Intent getActionIncDec(Context context, int min, int max, int value, long itemId) {
        Intent intent = new Intent(context, CommandService.class);
        intent.setAction(ACTION_INC_DEC);
        intent.putExtra(ARG_MIN, min);
        intent.putExtra(ARG_MAX, max);
        intent.putExtra(ARG_VALUE, value);
        intent.putExtra(ARG_ITEM, itemId);
        return intent;
    }

    public CommandService() {
        super("CommandService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Util.getApplicationComponent(this).inject(this);
    }

    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
        Realm realm = Realm.getDefaultInstance();
        if (intent != null) {
            final long itemId = intent.getLongExtra(ARG_ITEM,-1);
            final String action = intent.getAction();
            if (ACTION_COMMAND.equals(action) && itemId > 0) {
                final String command = intent.getStringExtra(ARG_COMMAND);
                ItemDB item = ItemDB.load(realm, itemId);
                handleActionCommand(command, item.toGeneric());
            }else if (ACTION_INC_DEC.equals(action) && itemId > 0) {
                final int min = intent.getIntExtra(ARG_MIN,0);
                final int max = intent.getIntExtra(ARG_MAX,0);
                final int value = intent.getIntExtra(ARG_VALUE,0);
                ItemDB item = ItemDB.load(realm, itemId);

                Communicator communicator = Communicator.instance(this);
                OHServer server = item.getServer().toGeneric();
                communicator.incDec(server, item.getName(), value, min, max);
            }
        }
        realm.close();
    }

    private void handleActionCommand(String command, OHItem item) {

        OHServer server = item.getServer();
        IServerHandler serverHandler = connectionFactory.createServerHandler (server, this);
        serverHandler.sendCommand(item.getName(), command);
    }
}
