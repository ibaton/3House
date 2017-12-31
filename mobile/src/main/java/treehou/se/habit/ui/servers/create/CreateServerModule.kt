package treehou.se.habit.ui.servers.create


import dagger.Module
import dagger.Provides
import treehou.se.habit.module.ViewModule

@Module
class CreateServerModule(activity: CreateServerActivity) : ViewModule<CreateServerActivity>(activity) {

    @Provides
    fun provideView(): CreateServerContract.View {
        return view
    }
}
