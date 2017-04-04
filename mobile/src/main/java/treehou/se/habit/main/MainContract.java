package treehou.se.habit.main;


import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import treehou.se.habit.mvp.BasePresenter;
import treehou.se.habit.mvp.BaseView;

public interface MainContract {

    interface View extends BaseView<Presenter> {
        void openSitemaps();
        void openSitemaps(String defaultSitemap);
        void openSitemap(OHSitemap ohSitemap);
        void openControllers();
        void openServers();
        void openSettings();
        boolean hasOpenPage();
    }

    interface Presenter extends BasePresenter {
        void showSitemaps();
        void showSitemap(OHSitemap ohSitemap);
        void showControllers();
        void showServers();
        void showSettings();
    }
}
