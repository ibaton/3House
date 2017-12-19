package treehou.se.habit.module;

import javax.inject.Singleton;

import dagger.Component;
import treehou.se.habit.BaseActivity;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.RestartBroadcastReceiver;
import treehou.se.habit.service.wear.VoiceActionService;
import treehou.se.habit.tasker.items.CommandActionFragment;
import treehou.se.habit.tasker.items.IncDecActionFragment;
import treehou.se.habit.tasker.items.SwitchActionFragment;
import treehou.se.habit.tasker.reciever.CommandReciever;
import treehou.se.habit.tasker.reciever.IncDecReciever;
import treehou.se.habit.ui.BaseDialogFragment;
import treehou.se.habit.ui.BaseFragment;
import treehou.se.habit.ui.bindings.BindingsFragment;
import treehou.se.habit.ui.colorpicker.ColorpickerActivity;
import treehou.se.habit.ui.control.CommandService;
import treehou.se.habit.ui.control.ControlFragment;
import treehou.se.habit.ui.control.ControllsFragment;
import treehou.se.habit.ui.control.EditControlFragment;
import treehou.se.habit.ui.control.EditControllerSettingsActivity;
import treehou.se.habit.ui.control.SliderActivity;
import treehou.se.habit.ui.control.cells.config.CellButtonConfigFragment;
import treehou.se.habit.ui.control.cells.config.CellIncDecConfigFragment;
import treehou.se.habit.ui.control.cells.config.CellSliderConfigFragment;
import treehou.se.habit.ui.control.cells.config.CellVoiceConfigFragment;
import treehou.se.habit.ui.homescreen.ControllerWidget;
import treehou.se.habit.ui.homescreen.ControllerWidgetConfigureActivity;
import treehou.se.habit.ui.homescreen.VoiceService;
import treehou.se.habit.ui.links.LinksListFragment;
import treehou.se.habit.ui.menu.NavigationDrawerFragment;
import treehou.se.habit.ui.servers.ServerMenuFragment;
import treehou.se.habit.ui.util.IconPickerActivity;
import treehou.se.habit.ui.widgets.factories.switches.RollerShutterWidgetHolder;

@Singleton
@Component(modules = {
        AndroidModule.class,
        AppBindingModule.class
})
public interface ApplicationComponent {
    void inject(HabitApplication application);
    void inject(BaseFragment fragment);
    void inject(BaseDialogFragment fragment);
    void inject(ControllsFragment fragment);
    void inject(ColorpickerActivity activity);
    void inject(BaseActivity activity);
    void inject(ControllerWidgetConfigureActivity activity);
    void inject(EditControllerSettingsActivity activity);
    void inject(IconPickerActivity activity);
    void inject(CellButtonConfigFragment fragment);
    void inject(NavigationDrawerFragment fragment);
    void inject(IncDecActionFragment fragment);
    void inject(CellVoiceConfigFragment fragment);
    void inject(SwitchActionFragment fragment);
    void inject(BindingsFragment fragment);
    void inject(ServerMenuFragment fragment);
    void inject(CellIncDecConfigFragment fragment);
    void inject(CellSliderConfigFragment fragment);
    void inject(RollerShutterWidgetHolder holder);
    void inject(SliderActivity activity);
    void inject(SliderActivity.SliderFragment fragment);
    void inject(EditControlFragment fragment);
    void inject(CommandActionFragment fragment);
    void inject(LinksListFragment fragment);
    void inject(ControlFragment fragment);
    void inject(VoiceActionService service);
    void inject(VoiceService service);
    void inject(CommandService service);
    void inject(RestartBroadcastReceiver receiver);
    void inject(ControllerWidget provider);
    void inject(CommandReciever receiver);
    void inject(IncDecReciever receiver);
}
