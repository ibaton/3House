package treehou.se.habit.dagger.fragment


import dagger.Module
import dagger.Provides
import treehou.se.habit.dagger.ViewModule
import treehou.se.habit.ui.servers.create.myopenhab.CreateMyOpenhabContract
import treehou.se.habit.ui.servers.create.myopenhab.CreateMyOpenhabFragment
import treehou.se.habit.ui.servers.create.myopenhab.CreateMyOpenhabPresenter

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
