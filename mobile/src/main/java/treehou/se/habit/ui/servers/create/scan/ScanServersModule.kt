package treehou.se.habit.ui.servers.create.custom


import dagger.Module
import dagger.Provides
import treehou.se.habit.module.ViewModule
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
