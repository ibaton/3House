package treehou.se.habit.ui.settings.subsettings.general


import dagger.Subcomponent
import treehou.se.habit.module.ActivityScope
import treehou.se.habit.module.FragmentComponent
import treehou.se.habit.module.FragmentComponentBuilder
import treehou.se.habit.ui.settings.SettingsFragment
import treehou.se.habit.ui.settings.SettingsModule

@ActivityScope
@Subcomponent(modules = arrayOf(GeneralSettingsModule::class))
interface GeneralSettingsComponent : FragmentComponent<GeneralSettingsFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<GeneralSettingsModule, GeneralSettingsComponent>
}