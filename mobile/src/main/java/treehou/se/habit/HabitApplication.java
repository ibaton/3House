package treehou.se.habit;

import android.content.Context;
import android.support.multidex.MultiDex;

import treehou.se.habit.connector.TrustModifier;

/**
 * Created by ibaton on 2015-03-25.
 */
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
