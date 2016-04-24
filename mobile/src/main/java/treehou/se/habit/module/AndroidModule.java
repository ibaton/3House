package treehou.se.habit.module;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.util.Settings;

@Module
public class AndroidModule {
    private final HabitApplication application;

    public AndroidModule(HabitApplication application) {
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
    public Vibrator provideVibratorManager(){
        return (Vibrator) application.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Provides @Singleton
    public Settings provideSettingsManager(){
        return Settings.instance(application);
    }
}
