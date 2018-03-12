package treehou.se.habit.gcm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.JobIntentService
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.gcm.GoogleCloudMessaging
import treehou.se.habit.R
import treehou.se.habit.ui.main.MainActivity
import treehou.se.habit.util.NotificationUtil


class GcmIntentService : JobIntentService() {

    private val TAG = GcmIntentService::class.java.simpleName

    val EXTRA_MSG = "message"
    val EXTRA_NOTIFICATION_ID = "notificationId"
    val ACTION_NOTIFICATION_SELECTED = "org.openhab.notification.selected"

    private var mNotificationManager: NotificationManager? = null


    override fun onHandleWork(intent: Intent) {
        val extras = intent.extras ?: return
        val notificationId: Int
        if (mNotificationManager == null)
            mNotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val gcm = GoogleCloudMessaging.getInstance(this)
        val messageType = gcm.getMessageType(intent)
        if (!extras.isEmpty) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE == messageType) {
                if (!intent.hasExtra(EXTRA_NOTIFICATION_ID)) {
                    notificationId = 1
                } else {
                    notificationId = Integer.parseInt(intent.extras!!.getString(EXTRA_NOTIFICATION_ID))
                }
                if ("notification" == intent.extras!!.getString("type")) {
                    val message = intent.extras!!.getString(EXTRA_MSG)
                    sendNotification(message, notificationId)
                } else if ("hideNotification" == intent.extras!!.getString("type")) {
                    Log.d(TAG, "Hide notification")
                    mNotificationManager!!.cancel(Integer.parseInt(intent.extras!!.getString(EXTRA_NOTIFICATION_ID)))
                }
            }
        }
    }

    private fun sendNotification(msg: String?, notificationId: Int) {
        if (mNotificationManager == null)
            mNotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationIntent = Intent(this, MainActivity::class.java)
                .setAction(ACTION_NOTIFICATION_SELECTED)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(EXTRA_NOTIFICATION_ID, notificationId)
                .putExtra(EXTRA_MSG, msg)

        val pendingNotificationIntent = PendingIntent.getActivity(applicationContext, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val mBuilder = NotificationCompat.Builder(this, NotificationUtil.CHANNEL_ID_NOTIFICATION)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.app_name))
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setAutoCancel(true)
                .setContentText(msg)
                .setContentIntent(pendingNotificationIntent)

        mNotificationManager!!.notify(notificationId, mBuilder.build())
    }

    companion object {
        val JOB_ID = 1000

        fun enqueueWork(context: Context, work: Intent) {
            JobIntentService.enqueueWork(context, GcmIntentService::class.java, JOB_ID, work)
        }
    }
}