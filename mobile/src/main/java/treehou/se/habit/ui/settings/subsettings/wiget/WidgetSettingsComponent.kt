package treehou.se.habit.ui.settings.subsettings.wiget


import dagger.Subcomponent
import treehou.se.habit.module.ActivityScope
import treehou.se.habit.module.FragmentComponent
import treehou.se.habit.module.FragmentComponentBuilder
import treehou.se.habit.ui.settings.subsettings.general.GeneralSettingsFragment
import treehou.se.habit.ui.settings.subsettings.general.GeneralSettingsModule

@ActivityScope
@Subcomponent(modules = arrayOf(WidgetSettingsModule::class))
interface WidgetSettingsComponent : FragmentComponent<WidgetSettingsFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<WidgetSettingsModule, WidgetSettingsComponent>
}