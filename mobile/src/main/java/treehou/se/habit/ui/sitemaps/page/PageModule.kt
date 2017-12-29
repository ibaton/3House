package treehou.se.habit.ui.sitemaps.page


import android.os.Bundle

import javax.inject.Named

import dagger.Module
import dagger.Provides
import treehou.se.habit.module.ViewModule

@Module
class PageModule(fragment: PageFragment, protected val args: Bundle) : ViewModule<PageFragment>(fragment) {

    @Provides
    fun provideView(): PageContract.View {
        return view
    }

    @Provides
    fun providePresenter(presenter: PagePresenter): PageContract.Presenter {
        return presenter
    }

    @Provides
    @Named("arguments")
    fun provideArgs(): Bundle {
        return args
    }
}
