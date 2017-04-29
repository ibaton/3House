package treehou.se.habit.main;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import javax.inject.Inject;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import treehou.se.habit.mvp.BaseDaggerActivity;
import treehou.se.habit.R;
import treehou.se.habit.module.HasActivitySubcomponentBuilders;
import treehou.se.habit.module.ServerLoaderFactory;
import treehou.se.habit.ui.control.ControllerUtil;
import treehou.se.habit.ui.control.ControllsFragment;
import treehou.se.habit.ui.menu.NavigationDrawerFragment;
import treehou.se.habit.ui.settings.SettingsFragment;
import treehou.se.habit.ui.servers.serverlist.ServersFragment;
import treehou.se.habit.ui.sitemaps.sitemap.SitemapFragment;
import treehou.se.habit.ui.sitemaps.sitemaplist.SitemapListFragment;
import treehou.se.habit.util.ConnectionFactory;
import treehou.se.habit.util.Settings;

import static treehou.se.habit.ui.menu.NavigationDrawerFragment.NavigationItems;


public class MainActivity extends BaseDaggerActivity<MainContract.Presenter>
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, MainContract.View {

    private static final String TAG = "MainActivity";

    @Inject Settings settings;
    @Inject MainPresenter mainPresenter;
    @Inject ConnectionFactory connectionFactory;
    @Inject ServerLoaderFactory serverLoaderFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(settings.getThemeResourse());

        setContentView(R.layout.activity_main);
        ControllerUtil.showNotifications(this);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // Fragment managing the behaviors, interactions and presentation of the navigation drawer.
        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        navigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        ControllerUtil.showNotifications(this);
    }

    @Override
    public MainContract.Presenter getPresenter() {
        return mainPresenter;
    }

    @Override
    protected void injectMembers(HasActivitySubcomponentBuilders hasActivitySubcomponentBuilders) {
        ((MainActivityComponent.Builder) hasActivitySubcomponentBuilders.getActivityComponentBuilder(MainActivity.class))
                .activityModule(new MainActivityModule(this))
                .build().injectMembers(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onSitemapItemSelected(OHSitemap sitemap) {
        mainPresenter.showSitemap(sitemap);
    }

    @Override
    public void onNavigationDrawerItemSelected(@NavigationDrawerFragment.NavigationItems int value) {
        switch (value) {
            case NavigationItems.ITEM_SITEMAPS:
                mainPresenter.showSitemaps();
                break;
            case NavigationItems.ITEM_CONTROLLERS:
                mainPresenter.showControllers();
                break;
            case NavigationItems.ITEM_SERVER:
                mainPresenter.showServers();
                break;
            case NavigationItems.ITEM_SETTINGS:
                mainPresenter.showSettings();
                break;
        }
    }

    public void openFragment(Fragment fragment){
        clearFragments();
        if(fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.page_container, fragment)
                    .commit();
        }
    }

    @Override
    public void openSitemaps() {
        SitemapListFragment fragment = SitemapListFragment.newInstance();
        openFragment(fragment);
    }

    @Override
    public void openSitemaps(String defaultSitemap) {
        SitemapListFragment fragment = SitemapListFragment.newInstance(defaultSitemap);
        openFragment(fragment);
    }

    @Override
    public void openSitemap(OHSitemap sitemap) {
        SitemapListFragment fragment = SitemapListFragment.newInstance(sitemap.getName());
        openFragment(fragment);
    }

    @Override
    public void openControllers() {
        ControllsFragment fragment = ControllsFragment.newInstance();
        openFragment(fragment);
    }

    @Override
    public void openServers() {
        ServersFragment fragment = ServersFragment.newInstance();
        openFragment(fragment);
    }

    @Override
    public void openSettings() {
        SettingsFragment fragment = SettingsFragment.newInstance();
        openFragment(fragment);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.page_container);
        if(fragment instanceof SitemapFragment){
            SitemapFragment sitemapFragment = (SitemapFragment) fragment;
            boolean result = sitemapFragment.removeAllPages();
            if(result) {
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public boolean hasOpenPage() {
        return getSupportFragmentManager().findFragmentById(R.id.page_container) != null;
    }

    /**
     * Clear fragments on backstack.
     */
    private void clearFragments(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.getBackStackEntryCount() > 0){
            fragmentManager.popBackStackImmediate();
        }
    }
}
