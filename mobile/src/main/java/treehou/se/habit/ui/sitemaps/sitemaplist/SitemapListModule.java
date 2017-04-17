package treehou.se.habit.ui.sitemaps.sitemaplist;


import android.os.Bundle;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import treehou.se.habit.module.ViewModule;

@Module
public class SitemapListModule extends ViewModule<SitemapListFragment> {

    protected final Bundle args;

    public SitemapListModule (SitemapListFragment fragment, Bundle args) {
        super(fragment);
        this.args = args;
    }

    @Provides
    public SitemapListContract.View provideView() {
        return view;
    }

    @Provides
    public SitemapListContract.Presenter providePresenter(SitemapListPresenter presenter) {
        return presenter;
    }

    @Provides
    @Named("arguments")
    public Bundle provideArgs() {
        return args;
    }
}
