package treehou.se.habit.dagger.fragment


import dagger.Subcomponent
import treehou.se.habit.dagger.FragmentComponent
import treehou.se.habit.dagger.FragmentComponentBuilder
import treehou.se.habit.dagger.scopes.ActivityScope
import treehou.se.habit.ui.servers.create.myopenhab.CreateMyOpenhabFragment

@ActivityScope
@Subcomponent(modules = arrayOf(CreateMyOpenhabModule::class))
interface CreateMyOpenhabComponent : FragmentComponent<CreateMyOpenhabFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<CreateMyOpenhabModule, CreateMyOpenhabComponent>
}