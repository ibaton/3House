package treehou.se.habit.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import javax.security.auth.callback.Callback;

import retrofit.RetrofitError;
import retrofit.client.Response;
import treehou.se.habit.Constants;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.db.ServerDB;

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

    /**
     * Register treehou.se.habit.gcm to listen for notifications
     */
    public static void gcmRegisterBackground(final Context context) {
        final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        new AsyncTask<Void,Void,String>() {

            @Override
            protected String doInBackground(Void... params) {
                String msg;
                try {
                    final String regId = gcm.register(Constants.GCM_SENDER_ID);
                    Log.e(TAG, "Registered gmc " + regId);
                    msg = "Device registered, registration ID=" + regId;

                    String deviceModel = URLEncoder.encode(Build.MODEL, "UTF-8");
                    String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

                    Communicator communicator = Communicator.instance(context);
                    List<ServerDB> servers = ServerDB.getServers();
                    for(final ServerDB server : servers) {

                        String regid = GCMHelper.getRegistrationId(context);
                        if (regid.isEmpty()) {
                            //continue; //TODO check if work
                        }

                        // Needs to have a my openhab acount for this to work
                        if (server != null &&
                                server.getRemoteUrl() != null &&
                                !server.getRemoteUrl().toLowerCase().startsWith("https://my.openhab.org")){
                            continue;
                        }

                        if(server != null && server.getUsername() != null && !server.getUsername().equals("") &&
                                server.getPassword() != null && !server.getPassword().equals("")) {

                            communicator.registerMyOpenhabGCM(server, deviceId, deviceModel, regId, new retrofit.Callback<String>() {
                                @Override
                                public void success(String regId, Response response) {
                                    Log.d(TAG, "GCM reg id success " + server.getUsername());
                                    GCMHelper.saveRegistrationId(context, regId);
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Log.e(TAG, "GCM reg id error: " + error + " " + server.getUsername());
                                }
                            });
                        }
                    }
                    // TODO show error message
                } catch (IOException e) {
                    msg = "Error :" + e.getMessage();
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }

                return msg;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }.execute();
    }

    public static boolean checkPlayServices(Activity context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, context, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    // TODO Implement multiple server registration, Not possible with my openhab, create new?
    public static void saveRegistrationId(Context context, String regId) {
        final SharedPreferences.Editor editor = getGCMPreferences(context).edit();
        editor.putString(PROPERTY_REG_ID, regId);
        int appVersion = getAppVersion(context);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
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
