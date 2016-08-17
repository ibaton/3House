package treehou.se.habit.module;


import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import com.google.gson.Gson;
import com.ning.http.client.Realm;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import se.treehou.ng.ohcommunicator.connector.GsonHelper;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.MainActivity;
import treehou.se.habit.ui.menu.NavigationDrawerFragment;
import treehou.se.habit.core.db.model.OHRealm;
import treehou.se.habit.ui.settings.subsettings.GeneralSettingsFragment;
import treehou.se.habit.ui.sitemaps.SitemapListFragment;
import treehou.se.habit.util.ConnectionFactory;
import treehou.se.habit.util.DatabaseServerLoaderFactory;
import treehou.se.habit.util.Settings;

@Module
public class AndroidModule {
    protected final Context application;

    public AndroidModule(Context application) {
        this.application = application;
    }

    @Provides @Singleton @ForApplication Context provideApplicationContext() {
        return application;
    }

    @Provides
    public android.content.SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    public OHRealm provideRealm() {
        return new OHRealm(application);
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

    @Provides
    public ServerLoaderFactory provideServerLoaderFactory(DatabaseServerLoaderFactory databaseServerLoaderFactory){
        return databaseServerLoaderFactory;
    }
}
