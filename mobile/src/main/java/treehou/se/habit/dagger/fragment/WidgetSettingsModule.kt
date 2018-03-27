package treehou.se.habit.dagger.fragment


import dagger.Module
import dagger.Provides
import treehou.se.habit.dagger.ViewModule
import treehou.se.habit.ui.settings.subsettings.wiget.WidgetSettingsContract
import treehou.se.habit.ui.settings.subsettings.wiget.WidgetSettingsFragment
import treehou.se.habit.ui.settings.subsettings.wiget.WidgetSettingsPresenter

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
