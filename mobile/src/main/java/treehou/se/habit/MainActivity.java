package treehou.se.habit;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import treehou.se.habit.connector.Communicator;
import treehou.se.habit.connector.requests.AuthRequest;
import treehou.se.habit.core.Server;
import treehou.se.habit.core.db.SitemapDB;
import treehou.se.habit.gcm.GCMHelper;
import treehou.se.habit.ui.about.AboutFragment;
import treehou.se.habit.ui.settings.SettingsFragment;
import treehou.se.habit.ui.settings.SetupServerFragment;
import treehou.se.habit.ui.ControllsFragment;
import treehou.se.habit.ui.ServersFragment;
import treehou.se.habit.ui.SitemapFragment;
import treehou.se.habit.ui.SitemapListFragment;
import treehou.se.habit.util.PrefSettings;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks{

    private static final String TAG = "MainActivity";
    public static final String GCM_SENDER_ID = "737820980945";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String EXTRA_SHOW_SITEMAP = "showSitemap";

    private GoogleCloudMessaging gcm;
    private String regid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));


        // Fragment managing the behaviors, interactions and presentation of the navigation drawer.
        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.findFragmentById(R.id.page_container) == null) {

            // Load server setup server fragment if no server found
            List<Server> servers = Server.getServers();
            if(servers.size() <= 0) {
                fragmentManager.beginTransaction()
                        .replace(R.id.page_container, ServersFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
                fragmentManager.beginTransaction()
                        .replace(R.id.page_container, SetupServerFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
            }else {
                // Load default sitemap if any
                long showSitemap = getIntent().getLongExtra(EXTRA_SHOW_SITEMAP, -1);
                SitemapDB defaultSitemap = PrefSettings.instance(this).getDefaultSitemap();
                if(savedInstanceState == null && showSitemap >= 0) {
                    fragmentManager.beginTransaction()
                        .replace(R.id.page_container, SitemapListFragment.newInstance(showSitemap))
                        .addToBackStack(null)
                        .commit();
                }else if (savedInstanceState == null && defaultSitemap != null){
                    fragmentManager.beginTransaction()
                            .replace(R.id.page_container, SitemapListFragment.newInstance(defaultSitemap.getId()))
                            .addToBackStack(null)
                            .commit();
                }else {
                    fragmentManager.beginTransaction()
                            .replace(R.id.page_container, SitemapListFragment.newInstance())
                            .addToBackStack(null)
                            .commit();
                }
            }
        }

        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            gcmRegisterBackground();
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.findFragmentByTag(SitemapFragment.TAG_SITEMAP_FRAGMENT) != null){
            SitemapFragment sitemapFragment = (SitemapFragment) fragmentManager.findFragmentByTag(SitemapFragment.TAG_SITEMAP_FRAGMENT);
            boolean result = sitemapFragment.popStack();
            if(result) {
                return;
            }
        }

        if(fragmentManager.getBackStackEntryCount() > 1){
            fragmentManager.popBackStack();
            return;
        }else{
            finish();
        }
        super.onBackPressed();
    }

    @Override
    public void onNavigationDrawerItemSelected(int value) {
        Fragment fragment = null;
        switch (value) {
            case (NavigationDrawerFragment.ITEM_SITEMAPS):
                fragment = SitemapListFragment.newInstance();
                break;
            case (NavigationDrawerFragment.ITEM_CONTROLLERS):
                fragment = ControllsFragment.newInstance();
                break;
            case (NavigationDrawerFragment.ITEM_SERVER):
                fragment = ServersFragment.newInstance();
                break;
            case (NavigationDrawerFragment.ITEM_SETTINGS):
                fragment = SettingsFragment.newInstance();
                break;
            case (NavigationDrawerFragment.ITEM_ABOUT):
                fragment = AboutFragment.newInstance();
                break;
        }

        clearFragments();
        if(fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.page_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * Clear fragments on backstack
     */
    public void clearFragments(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.getBackStackEntryCount() > 0){
            fragmentManager.popBackStackImmediate();
        }
    }

    /**
     * Register treehou.se.habit.gcm to listen for notifications
     */
    private void gcmRegisterBackground() {
        new AsyncTask<Void,Void,String>() {

            @Override
            protected String doInBackground(Void... params) {
                String msg;
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
                    }
                    final String regId = gcm.register(GCM_SENDER_ID);
                    Log.e(TAG, "Registered gmc " + regId);
                    msg = "Device registered, registration ID=" + regId;

                    String deviceModel = URLEncoder.encode(Build.MODEL, "UTF-8");
                    String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    String regUrl = "https://my.openhab.org/addAndroidRegistration?deviceId=" + deviceId +
                            "&deviceModel=" + deviceModel + "&regId=" + regId;
                    Communicator communicator = Communicator.instance(MainActivity.this);
                    List<Server> servers = Server.getServers();
                    for(final Server server : servers) {

                        regid = GCMHelper.getRegistrationId(MainActivity.this);
                        if (regid.isEmpty()) {
                            //continue; //TODO check if work
                        }

                        // Needs to have a my openhab acount for this to work
                        if (server != null &&
                                server.getRemoteUrl() != null &&
                                !server.getRemoteUrl().toLowerCase().startsWith("https://my.openhab.org")){
                            continue;
                        }

                        if(server.getUsername() != null && !server.getUsername().equals("") &&
                           server.getPassword() != null && !server.getPassword().equals("")) {

                            AuthRequest registerRequest = new AuthRequest(Request.Method.GET, regUrl, server.getUsername(), server.getPassword(), new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d(TAG, "GCM reg id success " + server.getUsername());

                                    GCMHelper.saveRegistrationId(MainActivity.this, regId);

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e(TAG, "GCM reg id error: " + error + " " + server.getUsername());
                                }
                            });
                            registerRequest.setRetryPolicy(new DefaultRetryPolicy( 5000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            communicator.addBasicRequest(registerRequest);
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

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
