package treehou.se.habit.dagger.fragment


import dagger.Subcomponent
import treehou.se.habit.dagger.FragmentComponent
import treehou.se.habit.dagger.FragmentComponentBuilder
import treehou.se.habit.dagger.scopes.ActivityScope
import treehou.se.habit.ui.settings.subsettings.general.GeneralSettingsFragment

@ActivityScope
@Subcomponent(modules = arrayOf(GeneralSettingsModule::class))
interface GeneralSettingsComponent : FragmentComponent<GeneralSettingsFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<GeneralSettingsModule, GeneralSettingsComponent>
}