package treehou.se.habit.data;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import treehou.se.habit.core.db.OHRealm;
import treehou.se.habit.core.db.OHRealmModule;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.module.AndroidModule;
import treehou.se.habit.module.ForApplication;
import treehou.se.habit.module.ServerLoaderFactory;
import treehou.se.habit.util.ConnectionFactory;

@Module
public class TestAndroidModule extends AndroidModule {

    public TestAndroidModule(Context application) {
        super(application);
    }

    @Provides
    @Singleton
    @ForApplication
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    public ServerLoaderFactory provideServerLoaderFactory(ConnectionFactory connectionFactory){
        return new TestServerLoaderFactory(connectionFactory);
    }

    class TestOHRealm extends OHRealm {

        public TestOHRealm(Context context) {
            super(context);
        }

        @Override
        public void setup(Context context) {
            Realm.setDefaultConfiguration(configuration());
            int size = Realm.getDefaultInstance().where(ServerDB.class).findAll().size();
            if(size <= 0) {
                ServerDB server = new ServerDB();
                ServerDB.Companion.save(server);
            }
        }

        @Override
        public RealmConfiguration configuration() {
            return new RealmConfiguration.Builder()
                    .modules(new OHRealmModule())
                    .name("treehou-test-2.realm")
                    .schemaVersion(1)
                    .build();

        }
    }
}
