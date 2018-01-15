package treehou.se.habit.tasker.reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log

import java.util.HashMap

import treehou.se.habit.tasker.boundle.CommandBoundleScrubber

class FireReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (treehou.se.habit.tasker.locale.Intent.ACTION_FIRE_SETTING != intent.action) {
            Log.e(TAG, "Received unexpected Intent action " + intent.action!!)
            return
        }
        Log.d(TAG, "Received Intent action " + intent.action!!)

        CommandBoundleScrubber.scrub(intent)

        val bundle = intent.getBundleExtra(treehou.se.habit.tasker.locale.Intent.EXTRA_BUNDLE)
        CommandBoundleScrubber.scrub(bundle)

        val type = bundle.getInt(IFireReciever.BUNDLE_EXTRA_TYPE, -1)
        val reciever = recievers[type]
        reciever?.fire(context, bundle) ?: Log.d(TAG, "No vald recievers found, Type: " + type + " Size: " + recievers.size)
    }

    companion object {

        private val TAG = "FireReceiver"

        private val recievers = HashMap<Int, IFireReciever>()

        init {
            recievers.put(CommandReciever.TYPE, CommandReciever())
            recievers.put(IncDecReciever.TYPE, IncDecReciever())
        }
    }
}