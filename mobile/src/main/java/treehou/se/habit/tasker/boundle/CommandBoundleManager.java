package treehou.se.habit.tasker.boundle;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import treehou.se.habit.core.Item;
import treehou.se.habit.tasker.reciever.CommandReciever;
import treehou.se.habit.tasker.reciever.IFireReciever;

/**
 * Created by ibaton on 2015-03-08.
 */
public class CommandBoundleManager {

    public static final String TAG = "CommandBoundleManager";

    public static final int TYPE_COMMAND = 1;

    /**
     * @param context Application context.
     * @param command The toast message to be displayed by the plug-in. Cannot be null.
     * @return A plug-in bundle.
     */
    public static Bundle generateCommandBundle(final Context context, final Item item, final String command) {

        Log.d(TAG, "Item " + item + " Command " + command);

        final Bundle result = new Bundle();
        result.putInt(IFireReciever.BUNDLE_EXTRA_TYPE, TYPE_COMMAND);
        result.putString(CommandReciever.BUNDLE_EXTRA_COMMAND, command);
        result.putLong(CommandReciever.BUNDLE_EXTRA_ITEM, item.getId());

        return result;
    }

    /**
     * Private constructor prevents instantiation
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private CommandBoundleManager() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
