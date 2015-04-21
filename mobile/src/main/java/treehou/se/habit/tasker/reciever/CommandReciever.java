package treehou.se.habit.tasker.reciever;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.Item;
import treehou.se.habit.tasker.boundle.CommandBoundleManager;

/**
 * Created by ibaton on 2015-03-08.
 */
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
            final long itemId = bundle.getLong(BUNDLE_EXTRA_ITEM);
            final String command = bundle.getString(BUNDLE_EXTRA_COMMAND);

            Item item = Item.load(Item.class, itemId);
            if(item != null){
                Communicator.instance(context).command(item.getServer(), item, command);
                Log.d(TAG, "Sent command " + command + " to item " + item.getName());
            }else {
                Log.d(TAG, "Item no longer exists");
            }

            Log.d(TAG, "Sending command " + command);
        }else {
            Log.d(TAG, "Boundle not valid.");
        }

        return false;
    }
}
