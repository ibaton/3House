package treehou.se.habit.core.db;

import io.realm.Realm;

public class DBHelper {

    /**
     * Generate a unique id for realm object
     * @param realm
     * @return
     */
    public synchronized static long getUniqueId(Realm realm, Class realmClass) {
        long id = 1;
        Number num = realm.where(realmClass).max("id");
        if (num != null) id = num.longValue() + 1;

        return id;
    }
}
