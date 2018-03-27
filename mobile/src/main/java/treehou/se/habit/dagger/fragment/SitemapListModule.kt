package treehou.se.habit.dagger.fragment


import android.os.Bundle
import dagger.Module
import dagger.Provides
import treehou.se.habit.dagger.ViewModule
import treehou.se.habit.ui.sitemaps.sitemaplist.SitemapListContract
import treehou.se.habit.ui.sitemaps.sitemaplist.SitemapListFragment
import treehou.se.habit.ui.sitemaps.sitemaplist.SitemapListPresenter
import javax.inject.Named

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
