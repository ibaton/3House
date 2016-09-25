package treehou.se.habit.module;

import android.support.v4.app.Fragment;

import javax.inject.Singleton;

import dagger.Component;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.MainActivity;
import treehou.se.habit.ui.colorpicker.ColorpickerActivity;
import treehou.se.habit.ui.control.SliderActivity;
import treehou.se.habit.ui.links.LinksListFragment;
import treehou.se.habit.ui.menu.NavigationDrawerFragment;
import treehou.se.habit.ui.servers.ServersFragment;
import treehou.se.habit.ui.servers.sitemaps.SitemapSelectFragment;
import treehou.se.habit.ui.settings.subsettings.GeneralSettingsFragment;
import treehou.se.habit.ui.sitemaps.PageFragment;
import treehou.se.habit.ui.sitemaps.SitemapFragment;
import treehou.se.habit.ui.sitemaps.SitemapListFragment;
import treehou.se.habit.ui.util.IconPickerActivity;
import treehou.se.habit.ui.widgets.factories.switches.RollerShutterWidgetHolder;

@Singleton
@Component(modules = AndroidModule.class)
public interface ApplicationComponent {
    void inject(HabitApplication application);
    void inject(ColorpickerActivity activity);
    void inject(IconPickerActivity activity);
    void inject(MainActivity activity);
    void inject(SitemapListFragment fragment);
    void inject(SitemapSelectFragment fragment);
    void inject(SitemapFragment fragment);
    void inject(GeneralSettingsFragment fragment);
    void inject(ServersFragment fragment);
    void inject(NavigationDrawerFragment fragment);
    void inject(Fragment fragment);
    void inject(PageFragment fragment);
    void inject(RollerShutterWidgetHolder holder);
    void inject(SliderActivity.SliderFragment fragment);
    void inject(LinksListFragment fragment);
}
