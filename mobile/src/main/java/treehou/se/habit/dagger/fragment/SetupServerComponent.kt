package treehou.se.habit.dagger.fragment


import dagger.Subcomponent
import treehou.se.habit.dagger.FragmentComponent
import treehou.se.habit.dagger.FragmentComponentBuilder
import treehou.se.habit.dagger.scopes.ActivityScope
import treehou.se.habit.ui.servers.create.custom.SetupServerFragment

@ActivityScope
@Subcomponent(modules = [(SetupServerModule::class)])
interface SetupServerComponent : FragmentComponent<SetupServerFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<SetupServerModule, SetupServerComponent>
}