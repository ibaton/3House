package treehou.se.habit.tasker.boundle;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import treehou.se.habit.tasker.reciever.IFireReciever;
import treehou.se.habit.tasker.reciever.IncDecReciever;

public class IncDecBoundleManager {

    public static final String TAG = "IncDecBoundleManager";

    public static final int TYPE_COMMAND = 3;

    /**
     * @param context Application context.
     * @param value The toast message to be displayed by the plug-in. Cannot be null.
     * @return A plug-in bundle.
     */
    public static Bundle generateCommandBundle(final Context context, final long itemId, final int value, final int min, final int max) {

        Log.d(TAG, "Item " + itemId + " inc/dec " + value);

        final Bundle result = new Bundle();
        result.putInt(IFireReciever.BUNDLE_EXTRA_TYPE, TYPE_COMMAND);
        result.putInt(IncDecReciever.BUNDLE_EXTRA_VALUE, value);
        result.putInt(IncDecReciever.BUNDLE_EXTRA_MIN, min);
        result.putInt(IncDecReciever.BUNDLE_EXTRA_MAX, max);
        result.putLong(IncDecReciever.BUNDLE_EXTRA_ITEM, itemId);

        return result;
    }

    /**
     * Private constructor prevents instantiation
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private IncDecBoundleManager() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
