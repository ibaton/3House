package treehou.se.habit.dagger.fragment


import dagger.Module
import dagger.Provides
import treehou.se.habit.dagger.ViewModule
import treehou.se.habit.ui.servers.sitemaps.list.SitemapSelectContract
import treehou.se.habit.ui.servers.sitemaps.list.SitemapSelectFragment
import treehou.se.habit.ui.servers.sitemaps.list.SitemapSelectPresenter

@Module
class SitemapSelectModule(fragment: SitemapSelectFragment) : ViewModule<SitemapSelectFragment>(fragment) {

    @Provides
    fun provideView(): SitemapSelectContract.View {
        return view
    }

    @Provides
    fun providePresenter(presenter: SitemapSelectPresenter): SitemapSelectContract.Presenter {
        return presenter
    }
}
