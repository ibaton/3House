package treehou.se.habit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import treehou.se.habit.ui.ServersFragment;
import treehou.se.habit.ui.SitemapListFragment;
import treehou.se.habit.ui.control.ControllsFragment;
import treehou.se.habit.ui.settings.SettingsFragment;
import treehou.se.habit.ui.settings.SetupServerFragment;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, packageName = "treehou.se.habit", sdk = 21)
public class MainActivityDrawerTest {

    @Test
    public void check_drawer_menu_sitemaps_item() throws Exception {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().get();

        activity.onNavigationDrawerItemSelected(NavigationDrawerFragment.ITEM_SITEMAPS);
        if(BuildConfig.DEBUG && !(activity.getSupportFragmentManager().findFragmentById(R.id.page_container) instanceof SitemapListFragment)) {
            throw new AssertionError();
        }
    }

    @Test
    public void check_drawer_menu_controllers_item() throws Exception {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().get();

        activity.onNavigationDrawerItemSelected(NavigationDrawerFragment.ITEM_CONTROLLERS);
        if(BuildConfig.DEBUG && !(activity.getSupportFragmentManager().findFragmentById(R.id.page_container) instanceof ControllsFragment)) {
            throw new AssertionError();
        }
    }

    @Test
    public void check_drawer_menu_servers_item() throws Exception {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().get();

        activity.onNavigationDrawerItemSelected(NavigationDrawerFragment.ITEM_SERVER);
        if(BuildConfig.DEBUG && !(activity.getSupportFragmentManager().findFragmentById(R.id.page_container) instanceof ServersFragment)) {
            throw new AssertionError();
        }
    }

    @Test
    public void check_drawer_menu_settings_item() throws Exception {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().get();

        activity.onNavigationDrawerItemSelected(NavigationDrawerFragment.ITEM_SETTINGS);
        if(BuildConfig.DEBUG && !(activity.getSupportFragmentManager().findFragmentById(R.id.page_container) instanceof SettingsFragment)) {
            throw new AssertionError();
        }
    }
}
