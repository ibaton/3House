package treehou.se.habit.gcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by ibaton on 2015-01-11.
 */
public class GCMHelper {

    static final String TAG = "GCMHelper";

    public static final String EXTRA_MESSAGE = "message";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final String SENDER_ID = "737820980945";
    private static final String PROPERTY_REG_ID = "registration_id";

    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);

        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    // TODO Implement multiple server registration, Not possible with my openhab, create new?
    public static void saveRegistrationId(Context context, String regId) {
        final SharedPreferences.Editor editor = getGCMPreferences(context).edit();
        editor.putString(PROPERTY_REG_ID, regId);
        int appVersion = getAppVersion(context);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
        Log.i(TAG, "Saving regId " + regId + " on app version " + appVersion);
    }

    // TODO Implement multiple server registration, Not possible with my openhab, create new?
    private static SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(
                GCMHelper.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}
