package treehou.se.habit.ui.main


import dagger.Subcomponent
import treehou.se.habit.module.ActivityComponent
import treehou.se.habit.module.ActivityComponentBuilder
import treehou.se.habit.module.ActivityScope

@ActivityScope
@Subcomponent(modules = arrayOf(MainActivityModule::class))
interface MainActivityComponent : ActivityComponent<MainActivity> {

    @Subcomponent.Builder
    interface Builder : ActivityComponentBuilder<MainActivityModule, MainActivityComponent>
}