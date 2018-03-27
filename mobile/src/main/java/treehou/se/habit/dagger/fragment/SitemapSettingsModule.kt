package treehou.se.habit.dagger.fragment


import dagger.Module
import dagger.Provides
import treehou.se.habit.dagger.ViewModule
import treehou.se.habit.ui.servers.sitemaps.sitemapsettings.SitemapSettingsContract
import treehou.se.habit.ui.servers.sitemaps.sitemapsettings.SitemapSettingsFragment
import treehou.se.habit.ui.servers.sitemaps.sitemapsettings.SitemapSettingsPresenter

@Module
class SitemapSettingsModule(fragment: SitemapSettingsFragment) : ViewModule<SitemapSettingsFragment>(fragment) {

    @Provides
    fun provideView(): SitemapSettingsContract.View {
        return view
    }

    @Provides
    fun providePresenter(presenter: SitemapSettingsPresenter): SitemapSettingsContract.Presenter {
        return presenter
    }
}
