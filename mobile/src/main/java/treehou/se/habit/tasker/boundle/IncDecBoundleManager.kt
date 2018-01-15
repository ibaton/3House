package treehou.se.habit.tasker.boundle

import android.content.Context
import android.os.Bundle
import android.util.Log

import treehou.se.habit.tasker.reciever.IFireReciever
import treehou.se.habit.tasker.reciever.IncDecReciever

class IncDecBoundleManager
/**
 * Private constructor prevents instantiation
 *
 * @throws UnsupportedOperationException because this class cannot be instantiated.
 */
private constructor() {

    init {
        throw UnsupportedOperationException("This class is non-instantiable") //$NON-NLS-1$
    }

    companion object {

        val TAG = "IncDecBoundleManager"

        val TYPE_COMMAND = 3

        /**
         * @param context Application context.
         * @param value The toast message to be displayed by the plug-in. Cannot be null.
         * @return A plug-in bundle.
         */
        fun generateCommandBundle(context: Context, itemId: Long, value: Int, min: Int, max: Int): Bundle {

            Log.d(TAG, "Item $itemId inc/dec $value")

            val result = Bundle()
            result.putInt(IFireReciever.BUNDLE_EXTRA_TYPE, TYPE_COMMAND)
            result.putInt(IncDecReciever.BUNDLE_EXTRA_VALUE, value)
            result.putInt(IncDecReciever.BUNDLE_EXTRA_MIN, min)
            result.putInt(IncDecReciever.BUNDLE_EXTRA_MAX, max)
            result.putLong(IncDecReciever.BUNDLE_EXTRA_ITEM, itemId)

            return result
        }
    }
}
