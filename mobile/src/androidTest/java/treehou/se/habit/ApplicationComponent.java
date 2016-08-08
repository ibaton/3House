package treehou.se.habit;

import android.support.v4.app.Fragment;

import javax.inject.Singleton;

import dagger.Component;
import treehou.se.habit.data.TestAndroidModule;
import treehou.se.habit.ui.menu.NavigationDrawerFragment;
import treehou.se.habit.ui.servers.ServersFragment;
import treehou.se.habit.ui.settings.subsettings.GeneralSettingsFragment;
import treehou.se.habit.ui.sitemaps.PageFragment;
import treehou.se.habit.ui.sitemaps.SitemapFragment;
import treehou.se.habit.ui.sitemaps.SitemapListFragment;
import treehou.se.habit.util.ConnectionFactory;
import treehou.se.habit.util.Settings;

@Singleton
@Component(modules = TestAndroidModule.class)
public interface ApplicationComponent {
    void inject(HabitApplication application);
    void inject(MainActivity homeActivity);
    void inject(SitemapListFragment sitemapListFragment);
    void inject(SitemapFragment sitemapFragment);
    void inject(GeneralSettingsFragment fragment);
    void inject(ServersFragment serversFragment);
    void inject(NavigationDrawerFragment drawerFragment);
    void inject(Fragment drawerFragment);
    void inject(PageFragment pageFragment);
    void inject(Settings settings);
    void inject(ConnectionFactory connectionFactory);
}
