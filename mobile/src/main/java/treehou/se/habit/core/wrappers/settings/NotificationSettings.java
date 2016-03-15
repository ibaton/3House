package treehou.se.habit.core.wrappers.settings;

import se.treehou.ng.ohcommunicator.core.db.OHRealm;
import treehou.se.habit.Constants;
import treehou.se.habit.core.db.settings.NotificationSettingsDB;

public class NotificationSettings {

    private static final String TAG = "NotificationSettings";
    public static final String PREF_GLOBAL = "NotificationSettings";
    public static final long ID_GLOBAL = 0;

    private NotificationSettingsDB notificationSettingsDB;

    public NotificationSettings() {
        super();
        notificationSettingsDB.setNotificationToSpeech(Constants.DEFAULT_NOTIFICATION_TO_SPEACH);
    }

    public NotificationSettings(NotificationSettingsDB notificationSettingsDB) {
        this.notificationSettingsDB = notificationSettingsDB;
    }

    public void setNotificationToSpeech(boolean notificationToSpeech) {
        notificationSettingsDB.setNotificationToSpeech(notificationToSpeech);
    }

    public NotificationSettingsDB getNotificationSettingsDB() {
        return notificationSettingsDB;
    }

    public void setNotificationSettingsDB(NotificationSettingsDB notificationSettingsDB) {
        this.notificationSettingsDB = notificationSettingsDB;
    }

    public boolean notificationToSpeech() {
        return notificationSettingsDB.isNotificationToSpeech();
    }

    public static NotificationSettings loadGlobal(){
        return new NotificationSettings(OHRealm.realm().where(NotificationSettingsDB.class).findFirst());
    }
}
