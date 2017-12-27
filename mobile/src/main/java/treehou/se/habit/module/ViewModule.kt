package treehou.se.habit.module


import dagger.Module
import dagger.Provides

@Module
abstract class ViewModule<out T>(protected val view: T) {

    @Provides
    @ActivityScope
    fun provideActivity(): T {
        return view
    }
}