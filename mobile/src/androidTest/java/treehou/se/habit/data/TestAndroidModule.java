package treehou.se.habit.data;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import treehou.se.habit.core.db.model.OHRealm;
import treehou.se.habit.core.db.model.OHRealmModule;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.module.AndroidModule;

public class TestAndroidModule extends AndroidModule {

    public TestAndroidModule(Context application) {
        super(application);
    }

    @Override
    public OHRealm provideRealm() {
        return new TestOHRealm(application);
    }

    class TestOHRealm extends OHRealm {

        public TestOHRealm(Context context) {
            super(context);
        }

        @Override
        public void setup(Context context) {
            Realm.setDefaultConfiguration(configuration(context));
            int size = Realm.getDefaultInstance().where(ServerDB.class).findAll().size();
            if(size <= 0) {
                ServerDB server = new ServerDB();
                ServerDB.save(server);
            }
        }

        @Override
        public RealmConfiguration configuration(Context context) {
            return new RealmConfiguration.Builder(context)
                    .modules(new OHRealmModule())
                    .name("treehou-test-2.realm")
                    .schemaVersion(1)
                    .build();

        }
    }
}
