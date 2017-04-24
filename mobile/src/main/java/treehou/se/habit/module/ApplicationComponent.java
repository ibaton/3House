package treehou.se.habit.module;

import javax.inject.Singleton;

import dagger.Component;
import treehou.se.habit.BaseActivity;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.service.wear.VoiceActionService;
import treehou.se.habit.tasker.items.CommandActionFragment;
import treehou.se.habit.tasker.items.IncDecActionFragment;
import treehou.se.habit.tasker.items.SwitchActionFragment;
import treehou.se.habit.ui.BaseFragment;
import treehou.se.habit.ui.bindings.BindingsFragment;
import treehou.se.habit.ui.colorpicker.ColorpickerActivity;
import treehou.se.habit.ui.colorpicker.LightFragment;
import treehou.se.habit.ui.control.CommandService;
import treehou.se.habit.ui.control.SliderActivity;
import treehou.se.habit.ui.control.config.CellButtonConfigFragment;
import treehou.se.habit.ui.control.config.CellIncDecConfigFragment;
import treehou.se.habit.ui.control.config.CellVoiceConfigFragment;
import treehou.se.habit.ui.control.config.cells.CellColorConfigFragment;
import treehou.se.habit.ui.homescreen.VoiceService;
import treehou.se.habit.ui.links.LinksListFragment;
import treehou.se.habit.ui.menu.NavigationDrawerFragment;
import treehou.se.habit.ui.servers.ServerMenuFragment;
import treehou.se.habit.ui.servers.ServersFragment;
import treehou.se.habit.ui.servers.sitemaps.SitemapSelectFragment;
import treehou.se.habit.ui.sitemaps.PageFragment;
import treehou.se.habit.ui.util.IconPickerActivity;
import treehou.se.habit.ui.widgets.factories.switches.RollerShutterWidgetHolder;

@Singleton
@Component(modules = {
        AndroidModule.class,
        AppBindingModule.class
})
public interface ApplicationComponent {
    void inject(HabitApplication application);
    void inject(BaseActivity activity);
    void inject(BaseFragment fragment);
    void inject(ColorpickerActivity activity);
    void inject(IconPickerActivity activity);
    void inject(SitemapSelectFragment fragment);
    void inject(CellButtonConfigFragment fragment);
    void inject(ServersFragment fragment);
    void inject(NavigationDrawerFragment fragment);
    void inject(IncDecActionFragment fragment);
    void inject(CellVoiceConfigFragment fragment);
    void inject(SwitchActionFragment fragment);
    void inject(CellColorConfigFragment fragment);
    void inject(BindingsFragment fragment);
    void inject(ServerMenuFragment fragment);
    void inject(CellIncDecConfigFragment fragment);
    void inject(LightFragment fragment);
    void inject(RollerShutterWidgetHolder holder);
    void inject(SliderActivity activity);
    void inject(SliderActivity.SliderFragment fragment);
    void inject(CommandActionFragment fragment);
    void inject(LinksListFragment fragment);
    void inject(VoiceActionService service);
    void inject(VoiceService service);
    void inject(CommandService service);
}
