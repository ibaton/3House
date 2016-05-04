package treehou.se.habit;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;
import se.treehou.ng.ohcommunicator.Openhab;
import treehou.se.habit.connector.TrustModifier;
import treehou.se.habit.core.db.model.OHRealm;
import treehou.se.habit.module.AndroidModule;
import treehou.se.habit.ui.sitemaps.SitemapListFragment;
import treehou.se.habit.util.Settings;

public class HabitApplication extends Application {

    private static final String TAG = HabitApplication.class.getSimpleName();

    @Singleton
    @Component(modules = AndroidModule.class)
    public interface ApplicationComponent {
        void inject(HabitApplication application);
        void inject(MainActivity homeActivity);
        void inject(SitemapListFragment sitemapListFragment);
        void inject(NavigationDrawerFragment drawerFragment);
        void inject(Settings settings);
    }

    protected ApplicationComponent component;

    @Inject OHRealm ohRealm;

    @Override
    public void onCreate() {
        super.onCreate();

        LeakCanary.install(this);

        if(component == null) {
            component = createComponent();
        }
        component().inject(this);

        ohRealm.setup(this);
        Openhab.setup(this);

        // TODO Remove when support for self signed certificates
        TrustModifier.NukeSSLCerts.nuke();
    }

    protected ApplicationComponent createComponent(){
        Log.d(TAG, "Creating app component");
        component = DaggerHabitApplication_ApplicationComponent.builder()
                .androidModule(new AndroidModule(this))
                .build();
        return component;
    }

    public void setTestComponent(ApplicationComponent appComponent) {
        component = appComponent;
    }

    public ApplicationComponent component() {
        return component;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        try {
            MultiDex.install(this);
        } catch (RuntimeException multiDexException) {
            multiDexException.printStackTrace();
        }
    }
}
