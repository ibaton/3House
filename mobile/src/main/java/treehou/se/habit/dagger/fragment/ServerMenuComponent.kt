package treehou.se.habit.dagger.fragment


import dagger.Subcomponent
import treehou.se.habit.dagger.FragmentComponent
import treehou.se.habit.dagger.FragmentComponentBuilder
import treehou.se.habit.dagger.scopes.ActivityScope
import treehou.se.habit.ui.servers.ServerMenuFragment

@ActivityScope
@Subcomponent(modules = arrayOf(ServerMenuModule::class))
interface ServerMenuComponent : FragmentComponent<ServerMenuFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<ServerMenuModule, ServerMenuComponent>
}