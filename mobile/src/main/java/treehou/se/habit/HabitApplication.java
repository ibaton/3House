package treehou.se.habit;

import android.content.Context;
import android.support.multidex.MultiDex;

import treehou.se.habit.connector.TrustModifier;

public class HabitApplication extends com.activeandroid.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        TrustModifier.NukeSSLCerts.nuke();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
