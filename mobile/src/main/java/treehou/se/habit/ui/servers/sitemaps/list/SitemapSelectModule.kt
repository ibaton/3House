package treehou.se.habit.ui.servers.sitemaps.list


import dagger.Module
import dagger.Provides
import treehou.se.habit.module.ViewModule

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
