package treehou.se.habit.core.db.settings;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NotificationSettingsDB extends RealmObject {

    private static final String TAG = "NotificationSettings";
    public static final String PREF_GLOBAL = "NotificationSettings";
    public static final long ID_GLOBAL = 0;

    @PrimaryKey
    private long id = 0;
    private boolean notificationToSpeech;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setNotificationToSpeech(boolean notificationToSpeech) {
        this.notificationToSpeech = notificationToSpeech;
    }

    public boolean notificationToSpeech() {
        return notificationToSpeech;
    }

    public static NotificationSettingsDB loadGlobal(Realm realm){
        NotificationSettingsDB settings = realm.where(NotificationSettingsDB.class).findFirst();
        if(settings == null){
            realm.beginTransaction();
            settings = realm.createObject(NotificationSettingsDB.class);
            settings.setId(ID_GLOBAL);
            realm.commitTransaction();
        }

        return settings;
    }
}
