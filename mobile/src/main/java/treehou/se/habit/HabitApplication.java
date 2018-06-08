package treehou.se.habit;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crash.FirebaseCrash;
import com.tspoon.traceur.Traceur;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.Map;

import javax.inject.Inject;

import treehou.se.habit.core.db.OHRealm;
import treehou.se.habit.dagger.ActivityComponentBuilder;
import treehou.se.habit.dagger.AndroidModule;
import treehou.se.habit.dagger.ApplicationComponent;
import treehou.se.habit.dagger.DaggerApplicationComponent;
import treehou.se.habit.dagger.FragmentComponentBuilder;
import treehou.se.habit.dagger.HasActivitySubcomponentBuilders;
import treehou.se.habit.gcm.GoogleCloudMessageConnector;
import treehou.se.habit.ui.control.ControllerHandler;
import treehou.se.habit.util.NotificationUtil;
import treehou.se.habit.util.Settings;

public class HabitApplication extends Application implements HasActivitySubcomponentBuilders {

    private static final String TAG = HabitApplication.class.getSimpleName();

    protected ApplicationComponent component;

    @Inject Map<Class<? extends Activity>, ActivityComponentBuilder> activityComponentBuilders;
    @Inject Map<Class<? extends Fragment>, FragmentComponentBuilder> fragmentComponentBuilders;
    @Inject OHRealm ohRealm;
    @Inject Settings settings;
    @Inject ControllerHandler controllHandler;
    @Inject NotificationUtil notificationUtil;
    @Inject GoogleCloudMessageConnector googleCloudMessageConnector;

    @Override
    public void onCreate() {
        setupFirebase();
        setupSimplifiedRxjavaDebugging();
        setupDagger();
        setTheme(settings.getThemeResourse());
        super.onCreate();
        JodaTimeAndroid.init(this);
        setupNotifications();
        //setupMyOpenhab(); Setup my openhab gcm connection
    }

    /**
     * Setup and initialize dagger.
     */
    private void setupDagger() {
        if (component == null) component = createComponent();
        component().inject(this);
    }

    /**
     * Setup notification channels and controller notifications.
     */
    private void setupNotifications() {
        notificationUtil.setup();
        controllHandler.init();
    }

    /**
     * Make it somewhat easier to find the rxjava sources that cased exceptions.
     */
    private void setupSimplifiedRxjavaDebugging() {
        Traceur.enableLogging();
    }

    /**
     * Setup firebase components
     */
    private void setupFirebase() {
        FirebaseApp.initializeApp(this);
        FirebaseCrash.setCrashCollectionEnabled(!BuildConfig.DEBUG);
    }

    /**
     * Setup connections to my openhab
     */
    private void setupMyOpenhab() {
        googleCloudMessageConnector.registerGcm(this);
    }

    protected ApplicationComponent createComponent() {
        Log.d(TAG, "Creating app component");
        return DaggerApplicationComponent.builder()
                .androidModule(new AndroidModule(this))
                .build();
    }

    public static HasActivitySubcomponentBuilders get(Context context) {
        return ((HasActivitySubcomponentBuilders) context.getApplicationContext());
    }

    public void setComponent(ApplicationComponent appComponent) {
        component = appComponent;
        component.inject(this);
    }

    public ApplicationComponent component() {
        return component;
    }

    @NonNull
    @Override
    public ActivityComponentBuilder getActivityComponentBuilder(@NonNull Class<? extends Activity> activityClass) {
        return activityComponentBuilders.get(activityClass);
    }

    @NonNull
    @Override
    public FragmentComponentBuilder getFragmentComponentBuilder(@NonNull Class<? extends Fragment> fragmentClass) {
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
