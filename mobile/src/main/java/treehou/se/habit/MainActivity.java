package treehou.se.habit;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import io.realm.Realm;
import io.realm.RealmResults;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.ui.control.ControlHelper;
import treehou.se.habit.ui.settings.SettingsFragment;
import treehou.se.habit.ui.control.ControllsFragment;
import treehou.se.habit.ui.ServersFragment;
import treehou.se.habit.ui.SitemapFragment;
import treehou.se.habit.ui.SitemapListFragment;
import treehou.se.habit.util.Settings;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks{

    private static final String TAG = "MainActivity";

    public static final String EXTRA_SHOW_SITEMAP = "showSitemap";

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();

        ControlHelper.showNotifications(this);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));


        // Fragment managing the behaviors, interactions and presentation of the navigation drawer.
        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        navigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.findFragmentById(R.id.page_container) == null) {

            // Load server setup server fragment if no server found
            RealmResults<ServerDB> serverDBs = realm.allObjects(ServerDB.class);

            if(serverDBs.size() <= 0) {
                fragmentManager.beginTransaction()
                        .replace(R.id.page_container, ServersFragment.newInstance())
                        .commit();
            }else {
                // Load default sitemap if any
                long defaultSitemap = Settings.instance(this).getDefaultSitemapId();
                if(savedInstanceState == null && defaultSitemap > 0) {

                    fragmentManager.beginTransaction()
                            .replace(R.id.page_container, SitemapListFragment.newInstance(defaultSitemap))
                            .commit();
                }else {
                    fragmentManager.beginTransaction()
                            .replace(R.id.page_container, SitemapListFragment.newInstance())
                            .commit();
                }
            }
        }

        /*if (GCMHelper.checkPlayServices(this)) {
            GCMHelper.gcmRegisterBackground(this);
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

        for(ControllerDB controller : OHTreehouseRealm.realm().allObjects(ControllerDB.class)){
            if (controller.isShowNotification()) {
                ControlHelper.showNotification(this, controller);
            }
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.close();
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
        Fragment fragment = fragmentManager.findFragmentById(R.id.page_container);
        if(fragment instanceof SitemapFragment){
            SitemapFragment sitemapFragment = (SitemapFragment) fragment;
            boolean result = sitemapFragment.popStack();
            if(result) {
                return;
            }
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
        }

        clearFragments();
        if(fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.page_container, fragment)
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
}
