package treehou.se.habit;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import javax.inject.Singleton;

import dagger.Component;
import se.treehou.ng.ohcommunicator.Openhab;
import treehou.se.habit.connector.TrustModifier;
import treehou.se.habit.core.db.model.OHRealm;
import treehou.se.habit.module.AndroidModule;
import treehou.se.habit.ui.sitemaps.SitemapListFragment;
import treehou.se.habit.util.Settings;

public class HabitApplication extends Application {

    @Singleton
    @Component(modules = AndroidModule.class)
    public interface ApplicationComponent {
        void inject(HabitApplication application);
        void inject(MainActivity homeActivity);
        void inject(SitemapListFragment sitemapListFragment);
        void inject(NavigationDrawerFragment drawerFragment);
        void inject(Settings settings);
    }

    private ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerHabitApplication_ApplicationComponent.builder()
                .androidModule(new AndroidModule(this))
                .build();
        component().inject(this);

        // TODO Remove when support for self signed certificates
        TrustModifier.NukeSSLCerts.nuke();
    }

    public ApplicationComponent component() {
        return component;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        OHRealm.setup(this);
        Openhab.setup(this);

        try {
            MultiDex.install(this);
        } catch (RuntimeException multiDexException) {
            multiDexException.printStackTrace();
        }
    }
}
