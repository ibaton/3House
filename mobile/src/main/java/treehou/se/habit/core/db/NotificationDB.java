package treehou.se.habit.core.db;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NotificationDB extends RealmObject {

    @PrimaryKey
    private long id = 0;
    private String message = "";
    private Date date;
    private boolean viewed;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    public static void save(NotificationDB item){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        if(item.getId() <= 0) {
            item.setId(getUniqueId());
        }
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
        realm.close();
    }

    public static long getUniqueId() {
        Realm realm = Realm.getDefaultInstance();
        Number num = realm.where(NotificationDB.class).max("id");
        long newId = 1;
        if (num != null) newId = num.longValue() + 1;
        realm.close();
        return newId;
    }
}
