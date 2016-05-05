package treehou.se.habit.module;


import android.content.Context;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import se.treehou.ng.ohcommunicator.connector.GsonHelper;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.MainActivity;
import treehou.se.habit.NavigationDrawerFragment;
import treehou.se.habit.core.db.model.OHRealm;
import treehou.se.habit.ui.sitemaps.SitemapListFragment;
import treehou.se.habit.util.Settings;

@Module
public class AndroidModule {
    protected final Context application;

    public AndroidModule(Context application) {
        this.application = application;
    }

    @Provides @Singleton @ForApplication  Context provideApplicationContext() {
        return application;
    }

    @Provides @Singleton
    public android.content.SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides @Singleton
    public OHRealm provideRealm() {
        return new OHRealm(application);
    }

    @Provides @Singleton
    public Gson provideGson() {
        return GsonHelper.createGsonBuilder();
    }

    @Provides @Singleton
    public Settings provideSettingsManager(){
        return Settings.instance(application);
    }

    @Singleton
    @Component(modules = AndroidModule.class)
    public interface ApplicationComponent {
        void inject(HabitApplication application);
        void inject(MainActivity homeActivity);
        void inject(SitemapListFragment sitemapListFragment);
        void inject(NavigationDrawerFragment drawerFragment);
        void inject(Settings settings);
    }
}
