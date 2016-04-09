package treehou.se.habit.core.db.model;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class OHRealm {

    public static Context context;

    public static void setup(Context context) {
        Realm.setDefaultConfiguration(configuration(context));
    }

    public static RealmConfiguration configuration() {
        return OHRealm.configuration(OHRealm.context);
    }

    public static RealmConfiguration configuration(Context context) {
        return new RealmConfiguration.Builder(context)
                .setModules(new OHRealmModule())
                .name("treehou.realm")
                .schemaVersion(1)
                .build();
    }

    public static Realm realm(){
        return Realm.getDefaultInstance();
    }
}
