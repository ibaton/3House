package treehou.se.habit;

import android.content.Context;
//import android.support.multidex.MultiDex;

import se.treehou.ng.ohcommunicator.Openhab;
import treehou.se.habit.connector.TrustModifier;

public class HabitApplication extends com.activeandroid.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO Remove when support for self signed certificates
        TrustModifier.NukeSSLCerts.nuke();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        Openhab.setup(base);

        try {
            //MultiDex.install(this);
        } catch (RuntimeException multiDexException) {
            // Work around Robolectric causing multi dex installation to fail, see
            // https://code.google.com/p/android/issues/detail?id=82007.
            boolean isUnderUnitTest;

            try {
                Class<?> robolectric = Class.forName("org.robolectric.Robolectric");
                isUnderUnitTest = (robolectric != null);
            } catch (ClassNotFoundException e) {
                isUnderUnitTest = false;
            }

            if (!isUnderUnitTest) {
                // Re-throw if this does not seem to be triggered by Robolectric.
                throw multiDexException;
            }
        }
    }
}
