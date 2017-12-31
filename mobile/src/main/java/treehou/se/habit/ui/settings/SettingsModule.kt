package treehou.se.habit.ui.settings


import dagger.Module
import dagger.Provides
import treehou.se.habit.module.ViewModule

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
