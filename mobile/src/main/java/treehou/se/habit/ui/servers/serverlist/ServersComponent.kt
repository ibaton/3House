package treehou.se.habit.ui.servers.serverlist


import dagger.Subcomponent
import treehou.se.habit.module.ActivityScope
import treehou.se.habit.module.FragmentComponent
import treehou.se.habit.module.FragmentComponentBuilder

@ActivityScope
@Subcomponent(modules = arrayOf(ServersModule::class))
interface ServersComponent : FragmentComponent<ServersFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<ServersModule, ServersComponent>
}