package treehou.se.habit.ui.servers.serverlist


import dagger.Module
import dagger.Provides
import treehou.se.habit.module.ViewModule

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
