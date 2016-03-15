package se.treehou.ng.ohcommunicator.core.db;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class OHRealm {

    public static Context context;
    private static RealmConfiguration configuration;

    public static void setup(Context context) {
        OHRealm.context = context;
    }

    public static void setup(RealmConfiguration configuration) {
        OHRealm.configuration = configuration;
    }

    public static RealmConfiguration configuration() {
        return configuration;
    }

    public static Realm realm() {

        if (configuration == null) {
            configuration = new RealmConfiguration.Builder(context)
                    .setModules(new OHRealmModule())
                    .build();
        }

        return Realm.getInstance(configuration);
    }
}
