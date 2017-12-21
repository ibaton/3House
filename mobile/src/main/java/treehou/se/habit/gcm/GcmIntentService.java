package treehou.se.habit.gcm;

/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *  @author Victor Belov
 *  @since 1.4.0
 *
 */

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Locale;

import io.realm.Realm;
import treehou.se.habit.main.MainActivity;
import treehou.se.habit.service.wear.VoiceActionService;
import treehou.se.habit.util.Constants;
import treehou.se.habit.R;
import treehou.se.habit.core.db.settings.NotificationSettingsDB;

public class GcmIntentService extends IntentService {

    private NotificationManager mNotificationManager;

    static final int NOTIFICATION_ID = 1337;
    private static final String GCM_FROM = "from";

    private static final String TAG = "GcmIntentService";

    // NotificationDB delete receiver

    private TextToSpeech textToSpeech;

    public GcmIntentService() {
        super("GcmIntentService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        if (extras == null) {
            return;
        }

        int notificationId;
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String gcmType = gcm.getMessageType(intent);
        Log.d(TAG, "Message type = " + gcmType);
        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(gcmType)) {
                // If this is notification, create new one
                if (!intent.hasExtra("notificationId")) {
                    notificationId = 1;
                } else {
                    notificationId = Integer.parseInt(intent.getExtras().getString("notificationId"));
                }
                String messageType = intent.getExtras().getString("type");
                if (messageType != null && messageType.equals("notification")) {
                    sendNotification(intent.getExtras().getString("message"), notificationId);
                    // If this is hideNotification, cancel existing notification with it's id
                } else if (messageType != null && messageType.equals("hideNotification")) {
                    mNotificationManager.cancel(Integer.parseInt(intent.getExtras().getString("notificationId")));
                }
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    private void sendNotification(final String msg, int notificationId) {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction("org.openhab.notification.selected");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra("notificationId", notificationId);

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Check if notification should be spoken

        Log.d(TAG, "Message " + Constants.PREF_REGISTRATION_SERVER + notificationId);

        /*getSharedPreferences(Constants.PREF_REGISTRATION_SERVER + notificationId, MODE_PRIVATE);
        SharedPreferences preferences = getSharedPreferences(Constants.PREFERENCE_SERVER, Context.MODE_PRIVATE);
        long serverId = preferences.getLong(Constants.PREF_REGISTRATION_SERVER+notificationId,-1);

        if(serverId < 0){
            return;
        }

        Server server = Server.load(Server.class, serverId);*/

        Realm realm = Realm.getDefaultInstance();
        NotificationSettingsDB notificationSettings = NotificationSettingsDB.loadGlobal(realm);
        if(notificationSettings.notificationToSpeech()) {
            textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        textToSpeech.setLanguage(Locale.getDefault());
                        textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            });
        }
        realm.close();

        /*NotificationDB notification = new NotificationDB(msg);
        notification.saveServer();
        List<NotificationDB> notifications = new Select().all().from(NotificationDB.class).execute();*/
        //TODO create inbox style

        String replyLabel = getString(R.string.notification_title);
        RemoteInput remoteInput = new RemoteInput.Builder(VoiceActionService.EXTRA_VOICE_REPLY)
                .setLabel(replyLabel)
                .build();

        Intent replyIntent = new Intent(this, VoiceActionService.class);
        PendingIntent replyPendingIntent = PendingIntent.getService(this, 0, replyIntent, 0);

        // Create the reply action and add the remote input
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                    R.drawable.action_voice_light,
                    getString(R.string.voice_command),
                    replyPendingIntent)
                .addRemoteInput(remoteInput)
                .build();

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(getString(R.string.notification_title))
                        .setAutoCancel(true)
                        .setSound(alarmSound)
                        .extend(new NotificationCompat.WearableExtender().addAction(action))
                        .setContentText(msg);

        mBuilder.setContentIntent(pendingNotificationIntent);

        mNotificationManager.notify(notificationId, mBuilder.build());
    }
}
