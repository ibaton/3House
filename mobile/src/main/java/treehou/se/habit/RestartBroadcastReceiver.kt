package treehou.se.habit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import javax.inject.Inject

import treehou.se.habit.ui.control.ControllerUtil

class RestartBroadcastReceiver : BroadcastReceiver() {

    @Inject lateinit var controllerUtil: ControllerUtil

    override fun onReceive(context: Context, intent: Intent) {
        (context.applicationContext as HabitApplication).component().inject(this)

        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            controllerUtil.showNotifications(context.applicationContext)
        }
    }
}
