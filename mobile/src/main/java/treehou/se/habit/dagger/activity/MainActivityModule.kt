package treehou.se.habit.dagger.activity

import dagger.Module
import dagger.Provides
import treehou.se.habit.dagger.ViewModule
import treehou.se.habit.ui.main.MainActivity
import treehou.se.habit.ui.main.MainContract

@Module
class MainActivityModule(activity: MainActivity) : ViewModule<MainActivity>(activity) {

    @Provides
    fun provideView(): MainContract.View {
        return view
    }
}
