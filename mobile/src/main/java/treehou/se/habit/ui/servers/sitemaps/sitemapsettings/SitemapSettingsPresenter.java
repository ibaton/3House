package treehou.se.habit.ui.servers.sitemaps.sitemapsettings;

import javax.inject.Inject;

import treehou.se.habit.module.RxPresenter;

public class SitemapSettingsPresenter extends RxPresenter implements SitemapSettingsContract.Presenter {

    private SitemapSettingsContract.View view;

    @Inject
    public SitemapSettingsPresenter(SitemapSettingsContract.View view) {
        this.view = view;
    }
}
