package treehou.se.habit;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.github.anrwatchdog.ANRWatchDog;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.Map;

import javax.inject.Inject;

import treehou.se.habit.core.db.model.OHRealm;
import treehou.se.habit.module.ActivityComponentBuilder;
import treehou.se.habit.module.AndroidModule;
import treehou.se.habit.module.ApplicationComponent;
import treehou.se.habit.module.DaggerApplicationComponent;
import treehou.se.habit.module.FragmentComponentBuilder;
import treehou.se.habit.module.HasActivitySubcomponentBuilders;
import treehou.se.habit.util.Settings;

public class HabitApplication extends Application implements HasActivitySubcomponentBuilders{

    private static final String TAG = HabitApplication.class.getSimpleName();

    protected ApplicationComponent component;

    @Inject Map<Class<? extends Activity>, ActivityComponentBuilder> activityComponentBuilders;
    @Inject Map<Class<? extends Fragment>, FragmentComponentBuilder> fragmentComponentBuilders;
    @Inject OHRealm ohRealm;
    @Inject Settings settings;

    @Override
    public void onCreate() {
        if(component == null) component = createComponent();
        component().inject(this);

        setTheme(settings.getThemeResourse());
        super.onCreate();

        new ANRWatchDog().start();

        JodaTimeAndroid.init(this);
        ohRealm.setup(this);
    }

    protected ApplicationComponent createComponent(){
        Log.d(TAG, "Creating app component");
        return DaggerApplicationComponent.builder()
                .androidModule(new AndroidModule(this))
                .build();
    }

    public static HasActivitySubcomponentBuilders get(Context context) {
        return ((HasActivitySubcomponentBuilders) context.getApplicationContext());
    }

    public void setTestComponent(ApplicationComponent appComponent) {
        component = appComponent;
    }

    public ApplicationComponent component() {
        return component;
    }

    @Override
    public ActivityComponentBuilder getActivityComponentBuilder(Class<? extends Activity> activityClass) {
        return activityComponentBuilders.get(activityClass);
    }

    @Override
    public FragmentComponentBuilder getFragmentComponentBuilder(Class<? extends Fragment> fragmentClass) {
        return fragmentComponentBuilders.get(fragmentClass);
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
