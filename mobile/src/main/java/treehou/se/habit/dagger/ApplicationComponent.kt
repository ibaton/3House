package treehou.se.habit.dagger

import dagger.Component
import treehou.se.habit.BaseActivity
import treehou.se.habit.HabitApplication
import treehou.se.habit.RestartBroadcastReceiver
import treehou.se.habit.service.VoiceService
import treehou.se.habit.service.wear.VoiceActionService
import treehou.se.habit.tasker.items.CommandActionFragment
import treehou.se.habit.tasker.items.IncDecActionFragment
import treehou.se.habit.tasker.items.SwitchActionFragment
import treehou.se.habit.tasker.reciever.CommandReciever
import treehou.se.habit.tasker.reciever.IncDecReciever
import treehou.se.habit.ui.BaseDialogFragment
import treehou.se.habit.ui.BaseFragment
import treehou.se.habit.ui.bindings.BindingsFragment
import treehou.se.habit.ui.colorpicker.ColorpickerActivity
import treehou.se.habit.ui.control.*
import treehou.se.habit.ui.control.cells.config.CellButtonConfigFragment
import treehou.se.habit.ui.control.cells.config.CellIncDecConfigFragment
import treehou.se.habit.ui.control.cells.config.CellSliderConfigFragment
import treehou.se.habit.ui.control.cells.config.CellVoiceConfigFragment
import treehou.se.habit.ui.homescreen.ControllerWidget
import treehou.se.habit.ui.homescreen.ControllerWidgetConfigureActivity
import treehou.se.habit.ui.links.LinksListFragment
import treehou.se.habit.ui.menu.NavigationDrawerFragment
import treehou.se.habit.ui.util.IconPickerActivity
import javax.inject.Singleton

/**
 * Insert fragments that does not have their own subcomponents
 */
@Singleton
@Component(modules = [(AndroidModule::class), (AppBindingModule::class)])
interface ApplicationComponent {
    fun inject(application: HabitApplication)

    fun inject(fragment: BaseFragment)

    fun inject(fragment: BaseDialogFragment)

    fun inject(fragment: ControllsFragment)

    fun inject(activity: ColorpickerActivity)

    fun inject(activity: BaseActivity)

    fun inject(activity: ControllerWidgetConfigureActivity)

    fun inject(activity: EditControllerSettingsActivity)

    fun inject(activity: IconPickerActivity)

    fun inject(fragment: CellButtonConfigFragment)

    fun inject(fragment: NavigationDrawerFragment)

    fun inject(fragment: IncDecActionFragment)

    fun inject(fragment: CellVoiceConfigFragment)

    fun inject(fragment: SwitchActionFragment)

    fun inject(fragment: BindingsFragment)

    fun inject(fragment: CellIncDecConfigFragment)

    fun inject(fragment: CellSliderConfigFragment)

    fun inject(activity: SliderActivity)

    fun inject(fragment: SliderActivity.SliderFragment)

    fun inject(fragment: EditControlFragment)

    fun inject(fragment: CommandActionFragment)

    fun inject(fragment: LinksListFragment)

    fun inject(fragment: ControlFragment)

    fun inject(service: VoiceActionService)

    fun inject(service: VoiceService)

    fun inject(service: CommandService)

    fun inject(receiver: RestartBroadcastReceiver)

    fun inject(provider: ControllerWidget)

    fun inject(receiver: CommandReciever)

    fun inject(receiver: IncDecReciever)
}
