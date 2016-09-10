package treehou.se.habit.module;

import android.support.v4.app.Fragment;

import javax.inject.Singleton;

import dagger.Component;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.MainActivity;
import treehou.se.habit.ui.control.SliderActivity;
import treehou.se.habit.ui.links.LinksListFragment;
import treehou.se.habit.ui.menu.NavigationDrawerFragment;
import treehou.se.habit.ui.servers.ServersFragment;
import treehou.se.habit.ui.servers.sitemaps.SitemapSelectFragment;
import treehou.se.habit.ui.settings.subsettings.GeneralSettingsFragment;
import treehou.se.habit.ui.sitemaps.PageFragment;
import treehou.se.habit.ui.sitemaps.SitemapFragment;
import treehou.se.habit.ui.sitemaps.SitemapListFragment;
import treehou.se.habit.ui.widgets.factories.switches.RollerShutterWidgetHolder;

@Singleton
@Component(modules = AndroidModule.class)
public interface ApplicationComponent {
    void inject(HabitApplication application);
    void inject(MainActivity homeActivity);
    void inject(SitemapListFragment sitemapListFragment);
    void inject(SitemapSelectFragment fragment);
    void inject(SitemapFragment sitemapFragment);
    void inject(GeneralSettingsFragment fragment);
    void inject(ServersFragment serversFragment);
    void inject(NavigationDrawerFragment drawerFragment);
    void inject(Fragment drawerFragment);
    void inject(PageFragment pageFragment);
    void inject(RollerShutterWidgetHolder holder);
    void inject(SliderActivity.SliderFragment fragment);
    void inject(LinksListFragment fragment);
}
