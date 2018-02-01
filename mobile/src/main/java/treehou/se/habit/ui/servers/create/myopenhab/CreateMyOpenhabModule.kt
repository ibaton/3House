package treehou.se.habit.ui.servers.create.myopenhab


import android.os.Bundle
import dagger.Module
import dagger.Provides
import treehou.se.habit.module.ViewModule

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
