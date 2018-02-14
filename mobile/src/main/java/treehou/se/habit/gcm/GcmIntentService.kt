package treehou.se.habit.gcm

import android.app.IntentService
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.gcm.GoogleCloudMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import treehou.se.habit.R
import treehou.se.habit.main.MainActivity
import treehou.se.habit.util.NotificationUtil
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.JobIntentService


class GcmIntentService : JobIntentService() {

    private val TAG = GcmIntentService::class.java.simpleName

    val EXTRA_MSG = "message"
    val EXTRA_NOTIFICATION_ID = "notificationId"
    val ACTION_NOTIFICATION_SELECTED = "org.openhab.notification.selected"
    val ACTION_NOTIFICATION_DELETED = "org.openhab.notification.deleted"

    private var mNotificationManager: NotificationManager? = null



    override fun onHandleWork(intent: Intent) {
        Log.d(TAG, "onReceive 2")
        val extras = intent!!.extras ?: return
        val notificationId: Int
        Log.d(TAG, "onReceive 3")
        if (mNotificationManager == null)
            mNotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Log.d(TAG, "onReceive 4")
        val gcm = GoogleCloudMessaging.getInstance(this)
        val messageType = gcm.getMessageType(intent)
        Log.d(TAG, "onReceive 5")
        if (!extras.isEmpty) {
            Log.d(TAG, "Message type = $messageType $extras " + (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE == messageType))
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE == messageType) {
                // If this is notification, create new one
                if (!intent.hasExtra(EXTRA_NOTIFICATION_ID)) {
                    notificationId = 1
                } else {
                    notificationId = Integer.parseInt(intent.extras!!.getString(EXTRA_NOTIFICATION_ID))
                }
                if ("notification" == intent.extras!!.getString("type")) {
                    Log.d(TAG, "Show notification ")
                    val message = intent.extras!!.getString(EXTRA_MSG)
                    Log.d(TAG, "Show notification $message")
                    sendNotification(message, notificationId)
                } else if ("hideNotification" == intent.extras!!.getString("type")) {
                    Log.d(TAG, "Hide notification")
                    mNotificationManager!!.cancel(Integer.parseInt(intent.extras!!.getString(EXTRA_NOTIFICATION_ID)))
                }
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        //GcmBroadcastReceiver.completeWakefulIntent(intent)
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
                .setColor(ContextCompat.getColor(this, R.color.openhab_orange))
                .setAutoCancel(true)
                .setContentText(msg)
                .setContentIntent(pendingNotificationIntent)

        mNotificationManager!!.notify(notificationId, mBuilder.build())
    }

    companion object {
        /**
         * Unique job ID for this service.
         */
        val JOB_ID = 1000

        /**
         * Convenience method for enqueuing work in to this service.
         */
        fun enqueueWork(context: Context, work: Intent) {
            JobIntentService.enqueueWork(context, GcmIntentService::class.java, JOB_ID, work)
        }
    }
}