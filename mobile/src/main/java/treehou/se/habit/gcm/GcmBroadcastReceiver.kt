/*
 * Copyright (c) 2010-2016, openHAB.org and others.
 *
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 */

package treehou.se.habit.gcm

import android.app.Activity
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.v4.app.JobIntentService
import android.support.v4.content.WakefulBroadcastReceiver
import android.util.Log

class GcmBroadcastReceiver : WakefulBroadcastReceiver() {
    private val mContext: Context? = null
    private val mNotificationManager: NotificationManager? = null

    override fun onReceive(context: Context, intent: Intent) {

        Log.d(TAG, "onReceive")

        // Explicitly specify that GcmIntentService will handle the intent.
        val comp = ComponentName(context.packageName,
                GcmIntentService::class.java.name)
        // Start the service, keeping the device awake while it is launching.
        GcmIntentService.enqueueWork(context, intent)
    }

    companion object {
        private val TAG = GcmBroadcastReceiver::class.java.simpleName
        private val mNotificationId = 0
    }

    /*    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String messageType = gcm.getMessageType(intent);
        Log.d(TAG, "Message type = " + messageType);
        Log.d(TAG, intent.getExtras().keySet().toString());
        if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            Log.d(TAG, "Send error: " + intent.getExtras().toString());
        } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
            Log.d(TAG, "Deleted messages on server: " +
                    intent.getExtras().toString());
        } else {
            Log.d(TAG, "Type: " + intent.getExtras().getString("type"));
            Log.d(TAG, "From: " + intent.getExtras().getString("from"));
            Log.d(TAG, "Collapse key: " + intent.getExtras().getString("collapse_key"));
            Log.d(TAG, "Message: " + intent.getExtras().getString("message"));
            if (intent.getExtras() != null)
                if (intent.getExtras().getString("type").equals("notification")) {
                    sendNotification(intent.getExtras().getString("message"));
                }
        }
        setResultCode(Activity.RESULT_OK);
    }

    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                new Intent(mContext, OpenHABMainActivity.class), 0);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mNotificationId++;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.openhabicon)
                        .setContentTitle("openHAB")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setAutoCancel(true)
                        .setSound(alarmSound)
                        .setContentText(msg);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(mNotificationId, mBuilder.build());
    }
*/

}
