package treehou.se.habit.core.db.model;

import android.content.Context;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class OHRealm {

    private Context context;

    public OHRealm(Context context) {
        this.context = context;
    }

    public void setup(Context context) {
        Realm.setDefaultConfiguration(configuration(context));
    }

    public RealmConfiguration configuration(Context context) {
        return new RealmConfiguration.Builder(context)
                .modules(new OHRealmModule())
                .name("treehou.realm")
                .schemaVersion(1)
                .build();
    }

    public Realm realm(){
        return Realm.getDefaultInstance();
    }
}
