package treehou.se.habit.core.db;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import se.treehou.ng.ohcommunicator.core.db.OHRealmModule;

public class OHTreehouseRealm {

    public static Context context;

    private OHTreehouseRealm() {}

    public static void setup(Context context){
        OHTreehouseRealm.context = context;
    }

    public static RealmConfiguration configuration() {
        return new RealmConfiguration.Builder(context)
                .name("treehouse6.realm")
                .setModules(Realm.getDefaultModule()/* new OHRealmModule()*/)
                .build();
    }

    public static Realm realm() {
        return Realm.getInstance(configuration());
    }
}
