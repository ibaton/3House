package treehou.se.habit.dagger


import dagger.Module
import dagger.Provides
import treehou.se.habit.dagger.scopes.ActivityScope

@Module
abstract class ViewModule<out T>(protected val view: T) {

    @Provides
    @ActivityScope
    fun provideActivity(): T {
        return view
    }
}