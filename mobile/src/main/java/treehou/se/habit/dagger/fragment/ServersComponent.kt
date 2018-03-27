package treehou.se.habit.dagger.fragment


import dagger.Subcomponent
import treehou.se.habit.dagger.FragmentComponent
import treehou.se.habit.dagger.FragmentComponentBuilder
import treehou.se.habit.dagger.scopes.ActivityScope
import treehou.se.habit.ui.servers.serverlist.ServersFragment

@ActivityScope
@Subcomponent(modules = arrayOf(ServersModule::class))
interface ServersComponent : FragmentComponent<ServersFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<ServersModule, ServersComponent>
}