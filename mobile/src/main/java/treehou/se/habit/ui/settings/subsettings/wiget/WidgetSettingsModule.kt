package treehou.se.habit.ui.settings.subsettings.wiget


import dagger.Module
import dagger.Provides
import treehou.se.habit.module.ViewModule
import treehou.se.habit.ui.settings.subsettings.general.GeneralSettingsContract
import treehou.se.habit.ui.settings.subsettings.general.GeneralSettingsFragment
import treehou.se.habit.ui.settings.subsettings.general.GeneralSettingsPresenter

@Module
class WidgetSettingsModule(fragment: WidgetSettingsFragment) : ViewModule<WidgetSettingsFragment>(fragment) {

    @Provides
    fun provideView(): WidgetSettingsContract.View {
        return view
    }

    @Provides
    fun providePresenter(presenter: WidgetSettingsPresenter): WidgetSettingsContract.Presenter {
        return presenter
    }
}
