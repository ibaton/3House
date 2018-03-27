package treehou.se.habit.dagger.fragment


import dagger.Module
import dagger.Provides
import treehou.se.habit.dagger.ViewModule
import treehou.se.habit.ui.servers.create.custom.SetupServerContract
import treehou.se.habit.ui.servers.create.custom.SetupServerFragment
import treehou.se.habit.ui.servers.create.custom.SetupServerPresenter

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
