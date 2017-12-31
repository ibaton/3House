package treehou.se.habit.ui.settings


import dagger.Subcomponent
import treehou.se.habit.module.ActivityScope
import treehou.se.habit.module.FragmentComponent
import treehou.se.habit.module.FragmentComponentBuilder

@ActivityScope
@Subcomponent(modules = arrayOf(SettingsModule::class))
interface SettingsComponent : FragmentComponent<SettingsFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<SettingsModule, SettingsComponent>
}