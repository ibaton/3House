package treehou.se.habit.main


import dagger.Module
import dagger.Provides
import treehou.se.habit.module.ViewModule

@Module
class MainActivityModule(activity: MainActivity) : ViewModule<MainActivity>(activity) {

    @Provides
    fun provideView(): MainContract.View {
        return view
    }
}
