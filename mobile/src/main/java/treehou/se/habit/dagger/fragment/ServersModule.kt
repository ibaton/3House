package treehou.se.habit.dagger.fragment


import dagger.Module
import dagger.Provides
import treehou.se.habit.dagger.ViewModule
import treehou.se.habit.ui.servers.serverlist.ServersContract
import treehou.se.habit.ui.servers.serverlist.ServersFragment
import treehou.se.habit.ui.servers.serverlist.ServersPresenter

@Module
class ServersModule(fragment: ServersFragment) : ViewModule<ServersFragment>(fragment) {

    @Provides
    fun provideView(): ServersContract.View {
        return view
    }

    @Provides
    fun providePresenter(presenter: ServersPresenter): ServersContract.Presenter {
        return presenter
    }
}
