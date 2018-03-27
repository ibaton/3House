package treehou.se.habit.dagger.fragment


import dagger.Subcomponent
import treehou.se.habit.dagger.FragmentComponent
import treehou.se.habit.dagger.FragmentComponentBuilder
import treehou.se.habit.dagger.scopes.ActivityScope
import treehou.se.habit.ui.colorpicker.LightFragment

@ActivityScope
@Subcomponent(modules = arrayOf(LightModule::class))
interface LightComponent : FragmentComponent<LightFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<LightModule, LightComponent>
}
