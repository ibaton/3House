package treehou.se.habit.tasker.reciever;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.db.ItemDB;
import treehou.se.habit.tasker.boundle.IncDecBoundleManager;

public class IncDecReciever implements IFireReciever {

    public static final String TAG = "IncDecReciever";

    public static final int TYPE = IncDecBoundleManager.TYPE_COMMAND;

    public static final String BUNDLE_EXTRA_VALUE   = "treehou.se.habit.extra.VALUE";
    public static final String BUNDLE_EXTRA_MIN     = "treehou.se.habit.extra.MIN";
    public static final String BUNDLE_EXTRA_MAX     = "treehou.se.habit.extra.MAX";
    public static final String BUNDLE_EXTRA_ITEM    = "treehou.se.habit.extra.ITEM";

    public boolean isBundleValid(Bundle bundle) {
        if (null == bundle) {
            Log.e(TAG, "Bundle cant be null");
            return false;
        }

        if (!bundle.containsKey(BUNDLE_EXTRA_VALUE)) {
            Log.e(TAG, String.format("bundle must contain extra %s", BUNDLE_EXTRA_VALUE));
            return false;
        }

        if (5 != bundle.keySet().size()) {
            Log.e(TAG, String.format("bundle must contain 5 keys, but currently contains %d keys: %s", bundle.keySet().size(), bundle.keySet()));
            return false;
        }

        return true;
    }


    @Override
    public boolean fire(Context context, Bundle bundle) {

        if (isBundleValid(bundle)) {
            final long itemId = bundle.getLong(BUNDLE_EXTRA_ITEM);

            final int min = bundle.getInt(BUNDLE_EXTRA_MIN);
            final int max = bundle.getInt(BUNDLE_EXTRA_MAX);
            final int range = Math.abs(max)+Math.abs(min);

            final int value = Math.max(Math.min(bundle.getInt(BUNDLE_EXTRA_VALUE), range), -range);

            ItemDB item = ItemDB.load(ItemDB.class, itemId);
            if(item != null){
                Communicator.instance(context).incDec(item.getServer(), item, value, min, max);
                Log.d(TAG, "Sent command " + value + " to item " + item.getName());
            }else {
                Log.d(TAG, "Item no longer exists");
            }
        }else {
            Log.d(TAG, "Boundle not valid.");
        }

        return false;
    }
}
