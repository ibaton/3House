package treehou.se.habit;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;

import treehou.se.habit.core.Server;
import treehou.se.habit.core.controller.Controller;
import treehou.se.habit.core.db.SitemapDB;
import treehou.se.habit.gcm.GCMHelper;
import treehou.se.habit.ui.about.AboutFragment;
import treehou.se.habit.ui.control.ControlHelper;
import treehou.se.habit.ui.settings.SettingsFragment;
import treehou.se.habit.ui.settings.SetupServerFragment;
import treehou.se.habit.ui.ControllsFragment;
import treehou.se.habit.ui.ServersFragment;
import treehou.se.habit.ui.SitemapFragment;
import treehou.se.habit.ui.SitemapListFragment;
import treehou.se.habit.util.Settings;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks{

    private static final String TAG = "MainActivity";

    public static final String EXTRA_SHOW_SITEMAP = "showSitemap";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ControlHelper.showNotifications(this);

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
                SitemapDB defaultSitemap = Settings.instance(this).getDefaultSitemap();
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

        if (GCMHelper.checkPlayServices(this)) {
            GCMHelper.gcmRegisterBackground(this);
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

        for(Controller controller : Controller.getControllers()){
            if (controller.showNotification()) {
                ControlHelper.showNotification(this, controller);
            }
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
}
