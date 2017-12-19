package treehou.se.habit.ui.servers.create.myopenhab


import dagger.Module
import dagger.Provides
import treehou.se.habit.module.ViewModule
import treehou.se.habit.ui.settings.SettingsFragment

@Module
class CreateMyOpenhabModule(fragment: CreateMyOpenhabFragment) : ViewModule<CreateMyOpenhabFragment>(fragment) {

    @Provides
    fun provideView(): CreateMyOpenhabContract.View {
        return view
    }

    @Provides
    fun providePresenter(presenter: CreateMyOpenhabPresenter): CreateMyOpenhabContract.Presenter {
        return presenter
    }
}
