package treehou.se.habit.ui.control;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.core.OHItemWrapper;
import se.treehou.ng.ohcommunicator.core.OHServerWrapper;
import treehou.se.habit.connector.Communicator;

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

    public static void startActionCommand(Context context, String command, OHItemWrapper item) {
        Intent intent = new Intent(context, CommandService.class);
        intent.setAction(ACTION_COMMAND);
        intent.putExtra(ARG_COMMAND, command);
        intent.putExtra(ARG_ITEM, item.getId());
        context.startService(intent);
    }

    public static Intent getActionCommand(Context context, String command, OHItemWrapper item) {
        Intent intent = new Intent(context, CommandService.class);
        intent.setAction(ACTION_COMMAND);
        intent.putExtra(ARG_COMMAND, command);
        intent.putExtra(ARG_ITEM, item.getId());
        return intent;
    }

    public static Intent getActionIncDec(Context context, int min, int max, int value, OHItemWrapper item) {
        Intent intent = new Intent(context, CommandService.class);
        intent.setAction(ACTION_INC_DEC);
        intent.putExtra(ARG_MIN, min);
        intent.putExtra(ARG_MAX, max);
        intent.putExtra(ARG_VALUE, value);
        intent.putExtra(ARG_ITEM, item.getId());
        return intent;
    }

    public CommandService() {
        super("CommandService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_COMMAND.equals(action)) {
                final String command = intent.getStringExtra(ARG_COMMAND);
                final int itemId = intent.getIntExtra(ARG_ITEM,-1);
                OHItemWrapper item = OHItemWrapper.load(itemId);
                handleActionCommand(command, item);
            }else if (ACTION_INC_DEC.equals(action)) {
                final int min = intent.getIntExtra(ARG_MIN,0);
                final int max = intent.getIntExtra(ARG_MAX,0);
                final int value = intent.getIntExtra(ARG_VALUE,0);
                final int itemId = intent.getIntExtra(ARG_ITEM, -1);
                OHItemWrapper item = OHItemWrapper.load(itemId);

                Communicator communicator = Communicator.instance(this);
                OHServerWrapper server = item.getServer();
                communicator.incDec(server, item, value, min, max);
            }
        }
    }

    private void handleActionCommand(String command, OHItemWrapper item) {

        OHServerWrapper server = item.getServer();
        Openhab.instance(server).sendCommand(item.getName(), command);
    }
}
