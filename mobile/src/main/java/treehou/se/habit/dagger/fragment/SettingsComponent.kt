package treehou.se.habit.dagger.fragment


import dagger.Subcomponent
import treehou.se.habit.dagger.FragmentComponent
import treehou.se.habit.dagger.FragmentComponentBuilder
import treehou.se.habit.dagger.scopes.ActivityScope
import treehou.se.habit.ui.settings.SettingsFragment

@ActivityScope
@Subcomponent(modules = arrayOf(SettingsModule::class))
interface SettingsComponent : FragmentComponent<SettingsFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<SettingsModule, SettingsComponent>
}