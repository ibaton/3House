package treehou.se.habit.dagger.fragment


import dagger.Module
import dagger.Provides
import treehou.se.habit.dagger.ViewModule
import treehou.se.habit.ui.settings.subsettings.general.GeneralSettingsContract
import treehou.se.habit.ui.settings.subsettings.general.GeneralSettingsFragment
import treehou.se.habit.ui.settings.subsettings.general.GeneralSettingsPresenter

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
