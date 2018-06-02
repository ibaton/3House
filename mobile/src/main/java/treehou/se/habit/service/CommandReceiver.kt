package treehou.se.habit.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import treehou.se.habit.ui.control.CommandService

class CommandReceiver() : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received command broadcast event")
        CommandService.enqueueWork(context, intent)
    }

    companion object {
        const val TAG = "CommandReceiver"
    }
}