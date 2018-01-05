package treehou.se.habit.ui.colorpicker


import dagger.Subcomponent
import treehou.se.habit.module.ActivityScope
import treehou.se.habit.module.FragmentComponent
import treehou.se.habit.module.FragmentComponentBuilder

@ActivityScope
@Subcomponent(modules = arrayOf(LightModule::class))
interface LightComponent : FragmentComponent<LightFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<LightModule, LightComponent>
}
