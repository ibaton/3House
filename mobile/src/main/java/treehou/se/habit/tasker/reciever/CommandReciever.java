package treehou.se.habit.tasker.reciever;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import treehou.se.habit.tasker.boundle.CommandBoundleManager;

public class CommandReciever implements IFireReciever {

    public static final String TAG = "CommandReciever";

    public static final int TYPE = CommandBoundleManager.TYPE_COMMAND;

    public static final String BUNDLE_EXTRA_COMMAND = "treehou.se.habit.extra.COMMAND";
    public static final String BUNDLE_EXTRA_ITEM    = "treehou.se.habit.extra.ITEM";

    public boolean isBundleValid(Bundle bundle) {
        if (null == bundle) {
            Log.e(TAG, "Bundle cant be null");
            return false;
        }

        if (!bundle.containsKey(BUNDLE_EXTRA_COMMAND)) {
            Log.e(TAG, String.format("bundle must contain extra %s", BUNDLE_EXTRA_COMMAND));
            return false;
        }

        if (3 != bundle.keySet().size()) {
            Log.e(TAG, String.format("bundle must contain 3 keys, but currently contains %d keys: %s", bundle.keySet().size(), bundle.keySet()));
            return false;
        }

        if (TextUtils.isEmpty(bundle.getString(BUNDLE_EXTRA_COMMAND))) {
            Log.e(TAG, String.format("bundle extra %s appears to be null or empty.  It must be a non-empty string", BUNDLE_EXTRA_COMMAND)); //$NON-NLS-1$
            return false;
        }

        return true;
    }


    @Override
    public boolean fire(Context context, Bundle bundle) {

        if (isBundleValid(bundle)) {
            final int itemId = bundle.getInt(BUNDLE_EXTRA_ITEM);
            final String command = bundle.getString(BUNDLE_EXTRA_COMMAND);

            OHItem item = null; // TODO OHItem.load(itemId);
            if(item != null){
                Openhab.instance(item.getServer()).sendCommand(item.getName(), command);
                Log.d(TAG, "Sent sendCommand " + command + " to item " + item.getName());
            }else {
                Log.d(TAG, "Item no longer exists");
            }

            Log.d(TAG, "Sending sendCommand " + command);
        }else {
            Log.d(TAG, "Boundle not valid.");
        }

        return false;
    }
}
