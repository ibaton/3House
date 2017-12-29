package treehou.se.habit.ui.sitemaps.sitemaplist


import android.os.Bundle

import javax.inject.Named

import dagger.Module
import dagger.Provides
import treehou.se.habit.module.ViewModule

@Module
class SitemapListModule(fragment: SitemapListFragment, protected val args: Bundle) : ViewModule<SitemapListFragment>(fragment) {

    @Provides
    fun provideView(): SitemapListContract.View {
        return view
    }

    @Provides
    fun providePresenter(presenter: SitemapListPresenter): SitemapListContract.Presenter {
        return presenter
    }

    @Provides
    @Named("arguments")
    fun provideArgs(): Bundle {
        return args
    }
}
