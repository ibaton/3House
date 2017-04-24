package treehou.se.habit.ui.sitemaps;


import android.os.Bundle;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import treehou.se.habit.module.ViewModule;
import treehou.se.habit.ui.sitemaps.sitemaplist.SitemapListContract;
import treehou.se.habit.ui.sitemaps.sitemaplist.SitemapListFragment;
import treehou.se.habit.ui.sitemaps.sitemaplist.SitemapListPresenter;

@Module
public class PageModule extends ViewModule<PageFragment> {

    protected final Bundle args;

    public PageModule(PageFragment fragment, Bundle args) {
        super(fragment);
        this.args = args;
    }

    @Provides
    public PageContract.View provideView() {
        return view;
    }

    @Provides
    public PageContract.Presenter providePresenter(PagePresenter presenter) {
        return presenter;
    }

    @Provides
    @Named("arguments")
    public Bundle provideArgs() {
        return args;
    }
}
