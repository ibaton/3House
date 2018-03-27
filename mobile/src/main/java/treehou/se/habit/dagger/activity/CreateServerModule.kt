package treehou.se.habit.dagger.activity


import dagger.Module
import dagger.Provides
import treehou.se.habit.dagger.ViewModule
import treehou.se.habit.ui.servers.create.CreateServerActivity
import treehou.se.habit.ui.servers.create.CreateServerContract

@Module
class CreateServerModule(activity: CreateServerActivity) : ViewModule<CreateServerActivity>(activity) {

    @Provides
    fun provideView(): CreateServerContract.View {
        return view
    }
}
