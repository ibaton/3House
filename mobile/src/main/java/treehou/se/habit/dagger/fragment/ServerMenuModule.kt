package treehou.se.habit.dagger.fragment


import dagger.Module
import dagger.Provides
import treehou.se.habit.dagger.ViewModule
import treehou.se.habit.ui.servers.ServerMenuContract
import treehou.se.habit.ui.servers.ServerMenuFragment
import treehou.se.habit.ui.servers.ServerMenuPresenter

@Module
class ServerMenuModule(fragment: ServerMenuFragment) : ViewModule<ServerMenuFragment>(fragment) {

    @Provides
    fun provideView(): ServerMenuContract.View {
        return view
    }

    @Provides
    fun providePresenter(presenter: ServerMenuPresenter): ServerMenuContract.Presenter {
        return presenter
    }
}
