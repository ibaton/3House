package treehou.se.habit.tasker.boundle

import android.os.Bundle
import android.util.Log

import treehou.se.habit.tasker.reciever.CommandReciever
import treehou.se.habit.tasker.reciever.IFireReciever

class CommandBoundleManager
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

        val TAG = "CommandBoundleManager"

        val TYPE_COMMAND = 1

        private val BUNDLE_KEY_VARIABLE_REPLACE_STRINGS = "net.dinglisch.android.tasker.extras.VARIABLE_REPLACE_KEYS"

        /**
         * @param command The toast message to be displayed by the plug-in. Cannot be null.
         * @return A plug-in bundle.
         */
        fun generateCommandBundle(itemId: Long, command: String): Bundle {

            Log.d(TAG, "Item $itemId Command $command")

            val result = Bundle()
            result.putInt(IFireReciever.BUNDLE_EXTRA_TYPE, TYPE_COMMAND)
            result.putString(CommandReciever.BUNDLE_EXTRA_COMMAND, command)
            // Instruct tasker to perform variable substitutions in the command
            result.putString(BUNDLE_KEY_VARIABLE_REPLACE_STRINGS, CommandReciever.BUNDLE_EXTRA_COMMAND)
            result.putLong(CommandReciever.BUNDLE_EXTRA_ITEM, itemId)

            return result
        }
    }
}
