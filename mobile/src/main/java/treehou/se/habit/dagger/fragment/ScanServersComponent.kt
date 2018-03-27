package treehou.se.habit.dagger.fragment


import dagger.Subcomponent
import treehou.se.habit.dagger.FragmentComponent
import treehou.se.habit.dagger.FragmentComponentBuilder
import treehou.se.habit.dagger.scopes.ActivityScope
import treehou.se.habit.ui.servers.create.scan.ScanServersFragment

@ActivityScope
@Subcomponent(modules = arrayOf(ScanServersModule::class))
interface ScanServersComponent : FragmentComponent<ScanServersFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<ScanServersModule, ScanServersComponent>
}