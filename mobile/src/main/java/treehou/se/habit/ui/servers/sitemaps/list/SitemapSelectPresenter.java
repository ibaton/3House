package treehou.se.habit.ui.servers.sitemaps.list;

import javax.inject.Inject;

import treehou.se.habit.module.RxPresenter;
import treehou.se.habit.ui.servers.serverlist.ServersContract;

public class SitemapSelectPresenter extends RxPresenter implements SitemapSelectContract.Presenter {

    private SitemapSelectContract.View view;

    @Inject
    public SitemapSelectPresenter(SitemapSelectContract.View view) {
        this.view = view;
    }
}
