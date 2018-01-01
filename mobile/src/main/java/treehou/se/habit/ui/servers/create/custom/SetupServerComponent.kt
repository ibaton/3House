package treehou.se.habit.ui.servers.create.custom


import dagger.Subcomponent
import treehou.se.habit.module.ActivityScope
import treehou.se.habit.module.FragmentComponent
import treehou.se.habit.module.FragmentComponentBuilder

@ActivityScope
@Subcomponent(modules = arrayOf(SetupServerModule::class))
interface SetupServerComponent : FragmentComponent<SetupServerFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<SetupServerModule, SetupServerComponent>
}