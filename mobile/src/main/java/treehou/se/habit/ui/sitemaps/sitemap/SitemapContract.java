package treehou.se.habit.ui.sitemaps.sitemap;


import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.mvp.BasePresenter;
import treehou.se.habit.mvp.BaseView;

public interface SitemapContract {

    interface View extends BaseView<Presenter> {
        void showPage(ServerDB server, OHLinkedPage page);
        boolean removeAllPages();
        boolean hasPage();
    }

    interface Presenter extends BasePresenter {
        String ARG_SITEMAP = "ARG_SITEMAP";
        String ARG_SERVER = "ARG_SERVER";

        void showPage(OHLinkedPage page);
    }
}
