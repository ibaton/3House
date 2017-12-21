package treehou.se.habit.ui.servers.sitemaps.list;


import dagger.Module;
import dagger.Provides;
import treehou.se.habit.module.ViewModule;

@Module
public class SitemapSelectModule extends ViewModule<SitemapSelectFragment> {

    public SitemapSelectModule(SitemapSelectFragment fragment) {
        super(fragment);
    }

    @Provides
    public SitemapSelectContract.View provideView() {
        return getView();
    }

    @Provides
    public SitemapSelectContract.Presenter providePresenter(SitemapSelectPresenter presenter) {
        return presenter;
    }
}
