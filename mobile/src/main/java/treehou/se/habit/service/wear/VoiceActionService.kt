package treehou.se.habit.service.wear

import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.RemoteInput
import android.util.Log

import javax.inject.Inject

import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.services.IServerHandler
import treehou.se.habit.HabitApplication
import treehou.se.habit.connector.Constants
import treehou.se.habit.util.ConnectionFactory

class VoiceActionService : IntentService("VoiceActionService") {

    @Inject lateinit var connectionFactory: ConnectionFactory

    init {

        (application as HabitApplication).component().inject(this)
        Log.d(TAG, "Constructor")
    }

    override fun onHandleIntent(intent: Intent?) {

        val command = getMessageText(intent)
        if (command != null) {
            Log.d(TAG, "Received sendCommand " + intent!!)

            val server: OHServer? = null
            // TODO server
            /*List<OHServer> servers = OHServer.loadAll();
            if(servers.size() > 0) {
                server = servers.get(0);
            }*/

            if (server != null) {
                val serverHandler = connectionFactory!!.createServerHandler(server, this)
                serverHandler.sendCommand(Constants.ITEM_VOICE_COMMAND, command)
            }
        }
    }

    private fun getMessageText(intent: Intent?): String? {
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        return if (remoteInput != null) {
            remoteInput.getCharSequence(EXTRA_VOICE_REPLY)!!.toString()
        } else null
    }

    companion object {

        private val TAG = "VoiceActionService"

        val EXTRA_VOICE_REPLY = "extra_voice_reply"
    }
}
