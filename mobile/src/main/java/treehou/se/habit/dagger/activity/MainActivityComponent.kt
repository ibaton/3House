package treehou.se.habit.dagger.activity


import dagger.Subcomponent
import treehou.se.habit.dagger.ActivityComponent
import treehou.se.habit.dagger.ActivityComponentBuilder
import treehou.se.habit.dagger.scopes.ActivityScope
import treehou.se.habit.ui.main.MainActivity

@ActivityScope
@Subcomponent(modules = [(MainActivityModule::class)])
interface MainActivityComponent : ActivityComponent<MainActivity> {

    @Subcomponent.Builder
    interface Builder : ActivityComponentBuilder<MainActivityModule, MainActivityComponent>
}