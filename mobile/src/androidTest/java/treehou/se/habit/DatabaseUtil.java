package treehou.se.habit;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import treehou.se.habit.core.db.model.OHRealmModule;
import treehou.se.habit.core.db.model.ServerDB;

public class DatabaseUtil {

    public static void init(Context context) {

        RealmConfiguration configuration = new RealmConfiguration.Builder(context)
                .modules(new OHRealmModule())
                .name("treehou-test.realm")
                .schemaVersion(1)
                .build();
        Realm.setDefaultConfiguration(configuration);

        int serverSize = Realm.getDefaultInstance().where(ServerDB.class).findAll().size();
        if(serverSize <= 0) {
            ServerDB server = new ServerDB();
            server.setName("Test Server");
            server.setLocalUrl("http://127.0.0.1:8080");
            ServerDB.save(server);
        }
    }
}
