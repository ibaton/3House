package treehou.se.habit.ui.servers


import dagger.Subcomponent
import treehou.se.habit.module.ActivityScope
import treehou.se.habit.module.FragmentComponent
import treehou.se.habit.module.FragmentComponentBuilder

@ActivityScope
@Subcomponent(modules = arrayOf(ServerMenuModule::class))
interface ServerMenuComponent : FragmentComponent<ServerMenuFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<ServerMenuModule, ServerMenuComponent>
}