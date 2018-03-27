package treehou.se.habit.dagger.fragment


import dagger.Module
import dagger.Provides
import treehou.se.habit.dagger.ViewModule
import treehou.se.habit.ui.settings.SettingsContract
import treehou.se.habit.ui.settings.SettingsFragment
import treehou.se.habit.ui.settings.SettingsPresenter

@Module
class SettingsModule(fragment: SettingsFragment) : ViewModule<SettingsFragment>(fragment) {

    @Provides
    fun provideView(): SettingsContract.View {
        return view
    }

    @Provides
    fun providePresenter(presenter: SettingsPresenter): SettingsContract.Presenter {
        return presenter
    }
}
