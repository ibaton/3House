package treehou.se.habit;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;


import io.realm.Realm;
import io.realm.RealmConfiguration;
import se.treehou.ng.ohcommunicator.Openhab;
import treehou.se.habit.connector.TrustModifier;
import treehou.se.habit.core.db.model.OHRealm;
import treehou.se.habit.core.db.settings.WidgetSettingsDB;

public class HabitApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO Remove when support for self signed certificates
        TrustModifier.NukeSSLCerts.nuke();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        OHRealm.setup(this);
        Openhab.setup(this);

        /* Realm realm = Realm.getDefaultInstance();
        WidgetSettingsDB settingsDB = realm.allObjects(WidgetSettingsDB.class).first();
        if(settingsDB == null){
            realm.beginTransaction();
            WidgetSettingsDB settings = realm.createObject(WidgetSettingsDB.class);
            settings.setId(1);
            settings.setIconSize(25);
            settings.setTextSize(25);
            realm.commitTransaction();
        }
        realm.close();*/
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
