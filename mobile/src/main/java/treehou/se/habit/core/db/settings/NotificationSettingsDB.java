package treehou.se.habit.core.db.settings;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import se.treehou.ng.ohcommunicator.core.db.OHRealm;
import treehou.se.habit.Constants;

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

    public boolean isNotificationToSpeech() {
        return notificationToSpeech;
    }

    public static void save(NotificationSettingsDB item){
        Realm realm = OHRealm.realm();
        realm.beginTransaction();
        if(item.getId() <= 0) {
            item.setId(getUniqueId());
        }
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
    }

    public static long getUniqueId() {
        Realm realm = OHRealm.realm();
        Number num = realm.where(NotificationSettingsDB.class).max("id");
        long newId = 1;
        if (num != null) newId = num.longValue() + 1;
        realm.close();
        return newId;
    }
}
