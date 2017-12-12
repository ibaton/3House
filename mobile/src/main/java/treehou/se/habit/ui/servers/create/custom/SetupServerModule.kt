package treehou.se.habit.ui.servers.create.custom


import dagger.Module
import dagger.Provides
import treehou.se.habit.module.ViewModule

@Module
class SetupServerModule(fragment: SetupServerFragment) : ViewModule<SetupServerFragment>(fragment) {

    @Provides
    fun provideView(): SetupServerContract.View {
        return view
    }

    @Provides
    fun providePresenter(presenter: SetupServerPresenter): SetupServerContract.Presenter {
        return presenter
    }
}
