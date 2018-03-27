package treehou.se.habit.dagger.fragment


import dagger.Module
import dagger.Provides
import treehou.se.habit.dagger.ViewModule
import treehou.se.habit.ui.servers.create.custom.ScanServersContract
import treehou.se.habit.ui.servers.create.custom.ScanServersPresenter
import treehou.se.habit.ui.servers.create.scan.ScanServersFragment

@Module
class ScanServersModule(fragment: ScanServersFragment) : ViewModule<ScanServersFragment>(fragment) {

    @Provides
    fun provideView(): ScanServersContract.View {
        return view
    }

    @Provides
    fun providePresenter(presenter: ScanServersPresenter): ScanServersContract.Presenter {
        return presenter
    }
}
