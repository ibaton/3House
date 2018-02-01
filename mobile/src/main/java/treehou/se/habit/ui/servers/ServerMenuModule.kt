package treehou.se.habit.ui.servers


import dagger.Module
import dagger.Provides
import treehou.se.habit.module.ViewModule

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
