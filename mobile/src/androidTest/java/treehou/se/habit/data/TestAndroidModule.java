package treehou.se.habit.data;

import android.content.Context;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import se.treehou.ng.ohcommunicator.connector.GsonHelper;
import treehou.se.habit.core.db.model.OHRealm;
import treehou.se.habit.core.db.model.OHRealmModule;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.module.AndroidModule;
import treehou.se.habit.module.ForApplication;
import treehou.se.habit.module.ServerLoaderFactory;
import treehou.se.habit.util.ConnectionFactory;
import treehou.se.habit.util.Settings;

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

    @Provides @Singleton
    public android.content.SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    public Gson provideGson() {
        return GsonHelper.createGsonBuilder();
    }

    @Provides
    public Settings provideSettingsManager(){
        return Settings.instance(application);
    }

    @Provides
    public ConnectionFactory provideConnectionFactory(){
        return new ConnectionFactory();
    }

    @Override
    public OHRealm provideRealm() {
        return new TestOHRealm(application);
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
