package treehou.se.habit.dagger.activity


import dagger.Subcomponent
import treehou.se.habit.dagger.ActivityComponent
import treehou.se.habit.dagger.ActivityComponentBuilder
import treehou.se.habit.dagger.scopes.ActivityScope
import treehou.se.habit.ui.servers.create.CreateServerActivity

@ActivityScope
@Subcomponent(modules = arrayOf(CreateServerModule::class))
interface CreateServerActivityComponent : ActivityComponent<CreateServerActivity> {

    @Subcomponent.Builder
    interface Builder : ActivityComponentBuilder<CreateServerModule, CreateServerActivityComponent>
}