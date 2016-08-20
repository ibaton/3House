package treehou.se.habit;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import net.danlew.android.joda.JodaTimeAndroid;

import javax.inject.Inject;

import treehou.se.habit.connector.TrustModifier;
import treehou.se.habit.core.db.model.OHRealm;
import treehou.se.habit.module.AndroidModule;
import treehou.se.habit.module.ApplicationComponent;
import treehou.se.habit.module.DaggerApplicationComponent;
import treehou.se.habit.util.Settings;

public class HabitApplication extends Application {

    private static final String TAG = HabitApplication.class.getSimpleName();

    protected ApplicationComponent component;

    @Inject OHRealm ohRealm;
    @Inject Settings settings;

    @Override
    public void onCreate() {
        if(component == null) {
            component = createComponent();
        }
        component().inject(this);

        setTheme(settings.getTheme());
        super.onCreate();

        JodaTimeAndroid.init(this);


        ohRealm.setup(this);

        // TODO Remove when support for self signed certificates
        TrustModifier.NukeSSLCerts.nuke();
    }

    protected ApplicationComponent createComponent(){
        Log.d(TAG, "Creating app component");
        ApplicationComponent component = DaggerApplicationComponent.builder()
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
