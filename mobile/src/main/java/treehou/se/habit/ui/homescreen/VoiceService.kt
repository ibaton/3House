package treehou.se.habit.ui.homescreen

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.support.v4.app.JobIntentService
import android.util.Log

import javax.inject.Inject

import io.realm.Realm
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.util.ConnectionFactory
import treehou.se.habit.util.Util

class VoiceService : JobIntentService() {

    @Inject lateinit var connectionFactory: ConnectionFactory

    override fun onCreate() {
        super.onCreate()
        Util.getApplicationComponent(this).inject(this)
    }

    override fun onHandleWork(intent: Intent) {
        Log.w(TAG, "onHandleIntent.")

        val serverId = intent.getLongExtra(EXTRA_SERVER, NULL_SERVER.toLong())
        if (NULL_SERVER.toLong() == serverId) {
            Log.w(TAG, "No server specified.")
            return
        }

        val realm = Realm.getDefaultInstance()
        val server = ServerDB.load(realm, serverId)

        val results = intent.extras!!.getStringArrayList(RecognizerIntent.EXTRA_RESULTS)
        if (results != null && !results.isEmpty() && server != null) {
            Log.d(TAG, "Received " + results.size + " voice results.")

            val command = results[0]
            val serverHandler = connectionFactory.createServerHandler(server.toGeneric(), this)
            serverHandler.sendCommand(VOICE_ITEM, command)
        }
        realm.close()
    }

    companion object {

        val TAG = "VoiceService"

        private val ACTION_COMMAND = "treehou.se.habit.ui.homescreen.action.VOICE"
        val EXTRA_SERVER = "extraServerId"

        val VOICE_ITEM = "VoiceCommand"

        private val NULL_SERVER = -1

        fun createVoiceCommand(context: Context, server: ServerDB): Intent {
            val intent = Intent(context, VoiceService::class.java)
            intent.action = ACTION_COMMAND
            intent.putExtra(EXTRA_SERVER, server.id)
            return intent
        }
    }
}
