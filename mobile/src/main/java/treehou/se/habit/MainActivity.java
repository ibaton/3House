package treehou.se.habit;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;


import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.core.db.model.controller.ControllerDB;
import treehou.se.habit.module.ApplicationComponent;
import treehou.se.habit.ui.control.ControlHelper;
import treehou.se.habit.ui.menu.NavigationDrawerFragment;
import treehou.se.habit.ui.settings.SettingsFragment;
import treehou.se.habit.ui.control.ControllsFragment;
import treehou.se.habit.ui.servers.ServersFragment;
import treehou.se.habit.ui.sitemaps.SitemapFragment;
import treehou.se.habit.ui.sitemaps.SitemapListFragment;
import treehou.se.habit.util.Settings;

import static treehou.se.habit.ui.menu.NavigationDrawerFragment.NavigationItems;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks{

    private static final String TAG = "MainActivity";

    private Realm realm;

    @Inject Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getApplicationComponent().inject(this);
        setTheme(settings.getTheme());
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

        setupFragments(savedInstanceState);

        showControllerInNotification();
    }

    /**
     * Setup the saved instance state.
     * @param savedInstanceState saved instance state
     */
    private void setupFragments(Bundle savedInstanceState){
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.findFragmentById(R.id.page_container) == null) {

            // Load server setup server fragment if no server found
            RealmResults<ServerDB> serverDBs = realm.where(ServerDB.class).findAll();

            if(serverDBs.size() <= 0) {
                fragmentManager.beginTransaction()
                        .replace(R.id.page_container, ServersFragment.newInstance())
                        .commit();
            }else {
                // Load default sitemap if any
                String defaultSitemap = settings.getDefaultSitemap();
                if(savedInstanceState == null && defaultSitemap != null) {
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
    }

    protected ApplicationComponent getApplicationComponent() {
        return ((HabitApplication) getApplication()).component();
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
    public void onNavigationDrawerItemSelected(@NavigationDrawerFragment.NavigationItems int value) {
        Fragment fragment = null;
        switch (value) {
            case NavigationItems.ITEM_SITEMAPS:
                fragment = SitemapListFragment.newInstance();
                break;
            case NavigationItems.ITEM_CONTROLLERS:
                fragment = ControllsFragment.newInstance();
                break;
            case NavigationItems.ITEM_SERVER:
                fragment = ServersFragment.newInstance();
                break;
            case NavigationItems.ITEM_SETTINGS:
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
     * Show controllers in notification tray
     */
    private void showControllerInNotification() {
        for(ControllerDB controller : realm.where(ControllerDB.class).findAll()){
            if (controller.isShowNotification()) {
                ControlHelper.showNotification(this, controller);
            }
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

    /**
     * Clear fragments on backstack
     */
    private void clearFragments(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.getBackStackEntryCount() > 0){
            fragmentManager.popBackStackImmediate();
        }
    }
}
