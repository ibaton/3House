package treehou.se.habit.ui.settings.subsettings.general


import dagger.Module
import dagger.Provides
import treehou.se.habit.module.ViewModule

@Module
class GeneralSettingsModule(fragment: GeneralSettingsFragment) : ViewModule<GeneralSettingsFragment>(fragment) {

    @Provides
    fun provideView(): GeneralSettingsContract.View {
        return view
    }

    @Provides
    fun providePresenter(presenter: GeneralSettingsPresenter): GeneralSettingsContract.Presenter {
        return presenter
    }
}
