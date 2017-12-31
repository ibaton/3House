package treehou.se.habit.ui.servers.sitemaps.sitemapsettings


import dagger.Module
import dagger.Provides
import treehou.se.habit.module.ViewModule

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
