package treehou.se.habit.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import treehou.se.habit.R
import javax.inject.Inject
import javax.inject.Singleton

class NotificationUtil @Inject constructor() {

    @Inject lateinit var context: Context

    fun setup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createTrustManagerNotificationChannel(context)
            createControllersNotificationChannel(context)
            createGcmNotificationChannel(context)
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createTrustManagerNotificationChannel(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = CHANNEL_ID_TRUST_MANAGER
        val channelName = context.getString(R.string.channel_name_certificates)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel = NotificationChannel(channelId, channelName, importance)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createControllersNotificationChannel(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = CHANNEL_ID_CONTROLLERS
        val channelName = context.getString(R.string.channel_name_controllers)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel = NotificationChannel(channelId, channelName, importance)
        notificationChannel.enableVibration(false)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createGcmNotificationChannel(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = CHANNEL_ID_NOTIFICATION
        val channelName = context.getString(R.string.channel_name_notification)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel = NotificationChannel(channelId, channelName, importance)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    companion object {
        @JvmField val CHANNEL_ID_TRUST_MANAGER = "TRUST_MANAGER"
        @JvmField val CHANNEL_ID_CONTROLLERS = "CONTROLLERS"
        @JvmField val CHANNEL_ID_NOTIFICATION = "NOTIFICATION"
    }
}