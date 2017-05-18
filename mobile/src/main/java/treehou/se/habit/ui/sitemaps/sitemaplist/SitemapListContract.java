package treehou.se.habit.ui.sitemaps.sitemaplist;

import java.util.List;

import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import treehou.se.habit.mvp.BasePresenter;
import treehou.se.habit.mvp.BaseView;

public interface SitemapListContract {

    interface View extends BaseView<Presenter> {
        void showSitemap(OHServer server, OHSitemap sitemap);
        void clearList();
        void hideEmptyView();
        void showServerError(OHServer server, Throwable error);
        void populateSitemaps(OHServer server, List<OHSitemap> sitemaps);
    }

    interface Presenter extends BasePresenter {
        void openSitemap(OHServer server, OHSitemap sitemap);
        void reloadSitemaps(OHServer server);
    }
}
