package treehou.se.habit;

import io.realm.Realm;
import io.realm.RealmResults;
import treehou.se.habit.core.db.model.ServerDB;

public class DatabaseUtil {

    public static void init() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ServerDB> serverDBs = realm.allObjects(ServerDB.class);

        if(serverDBs.size() <= 0){
            realm.beginTransaction();
            ServerDB serverDB = new ServerDB();
            realm.copyToRealm(serverDB);
            realm.commitTransaction();
        }
        realm.close();
    }
}
