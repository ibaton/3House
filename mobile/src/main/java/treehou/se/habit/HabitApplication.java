package treehou.se.habit;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.core.db.OHRealm;
import treehou.se.habit.connector.TrustModifier;
import treehou.se.habit.core.db.OHTreehouseRealm;

public class HabitApplication extends Application /*com.activeandroid.app.Application*/ {

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO Remove when support for self signed certificates
        TrustModifier.NukeSSLCerts.nuke();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        //OHTreehouseRealm.setup(this);
        //RealmConfiguration realmConfiguration = OHTreehouseRealm.configuration();
        //Realm.setDefaultConfiguration(realmConfiguration); // Make this Realm the default

        //Realm.deleteRealm(OHRealm.configuration());
        //Realm.deleteRealm(OHTreehouseRealm.configuration());

        //OHRealm.setup(OHTreehouseRealm.configuration());
        //Openhab.setup(base);

        try {
            MultiDex.install(this);
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

    @Override
    public void onTerminate() {
        super.onTerminate();

        Openhab.stop();
    }
}
