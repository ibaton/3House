package treehou.se.habit.ui.servers.sitemaps.sitemapsettings;


import dagger.Module;
import dagger.Provides;
import treehou.se.habit.module.ViewModule;

@Module
public class SitemapSettingsModule extends ViewModule<SitemapSettingsFragment> {

    public SitemapSettingsModule(SitemapSettingsFragment fragment) {
        super(fragment);
    }

    @Provides
    public SitemapSettingsContract.View provideView() {
        return getView();
    }

    @Provides
    public SitemapSettingsContract.Presenter providePresenter(SitemapSettingsPresenter presenter) {
        return presenter;
    }
}
