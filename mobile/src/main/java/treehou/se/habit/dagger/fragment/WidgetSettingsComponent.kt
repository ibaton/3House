package treehou.se.habit.dagger.fragment


import dagger.Subcomponent
import treehou.se.habit.dagger.FragmentComponent
import treehou.se.habit.dagger.FragmentComponentBuilder
import treehou.se.habit.dagger.scopes.ActivityScope
import treehou.se.habit.ui.settings.subsettings.wiget.WidgetSettingsFragment

@ActivityScope
@Subcomponent(modules = arrayOf(WidgetSettingsModule::class))
interface WidgetSettingsComponent : FragmentComponent<WidgetSettingsFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<WidgetSettingsModule, WidgetSettingsComponent>
}