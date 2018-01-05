package treehou.se.habit.ui.control

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import io.realm.Realm
import se.treehou.ng.ohcommunicator.connector.models.OHItem
import treehou.se.habit.connector.Communicator
import treehou.se.habit.core.db.model.ItemDB
import treehou.se.habit.util.ConnectionFactory
import treehou.se.habit.util.Util
import javax.inject.Inject

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *
 *
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class CommandService : IntentService("CommandService") {

    @Inject lateinit var connectionFactory: ConnectionFactory

    override fun onCreate() {
        super.onCreate()
        Util.getApplicationComponent(this).inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "onHandleIntent")
        val realm = Realm.getDefaultInstance()
        if (intent != null) {
            val itemId = intent.getLongExtra(ARG_ITEM, -1)
            val action = intent.action
            if (ACTION_COMMAND == action && itemId > 0) {
                val command = intent.getStringExtra(ARG_COMMAND)
                val item = ItemDB.load(realm, itemId)
                handleActionCommand(command, item.toGeneric())
            } else if (ACTION_INC_DEC == action && itemId > 0) {
                val min = intent.getIntExtra(ARG_MIN, 0)
                val max = intent.getIntExtra(ARG_MAX, 0)
                val value = intent.getIntExtra(ARG_VALUE, 0)
                val item = ItemDB.load(realm, itemId)

                val communicator = Communicator.instance(this)
                val server = item.server.toGeneric()
                communicator.incDec(server, item.name, value, min, max)
            }
        }
        realm.close()
    }

    private fun handleActionCommand(command: String, item: OHItem) {

        val server = item.server
        val serverHandler = connectionFactory.createServerHandler(server, this)
        serverHandler.sendCommand(item.name, command)
    }

    companion object {

        private val TAG = "CommandService"

        private val ARG_ITEM = "ARG_ITEM"

        private val ACTION_COMMAND = "ACTION_COMMAND"
        private val ARG_COMMAND = "ARG_COMMAND"

        private val ACTION_INC_DEC = "ACTION_INC_DEC"
        private val ARG_MAX = "ARG_MAX"
        private val ARG_MIN = "ARG_MIN"
        private val ARG_VALUE = "ARG_VALUE"

        fun startActionCommand(context: Context, command: String, item: OHItem) {
            val intent = Intent(context, CommandService::class.java)
            intent.action = ACTION_COMMAND
            intent.putExtra(ARG_COMMAND, command)
            intent.putExtra(ARG_ITEM, item.id)
            context.startService(intent)
        }

        fun getActionCommand(context: Context, command: String, itemId: Long): Intent {
            val intent = Intent(context, CommandService::class.java)
            intent.action = ACTION_COMMAND
            intent.putExtra(ARG_COMMAND, command)
            intent.putExtra(ARG_ITEM, itemId)
            return intent
        }

        fun getActionIncDec(context: Context, min: Int, max: Int, value: Int, itemId: Long): Intent {
            val intent = Intent(context, CommandService::class.java)
            intent.action = ACTION_INC_DEC
            intent.putExtra(ARG_MIN, min)
            intent.putExtra(ARG_MAX, max)
            intent.putExtra(ARG_VALUE, value)
            intent.putExtra(ARG_ITEM, itemId)
            return intent
        }
    }
}
