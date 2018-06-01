package treehou.se.habit.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class VoiceReceiver() : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received voice broadcast event")
        VoiceService.enqueueWork(context, intent)
    }

    companion object {
        const val TAG = "VoiceReceiver"
    }
}