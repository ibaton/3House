package treehou.se.habit.ui.sitemaps.page;


import java.util.List;

import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import treehou.se.habit.mvp.BasePresenter;
import treehou.se.habit.mvp.BaseView;
import treehou.se.habit.ui.widgets.WidgetFactory;

public interface PageContract {
    String ARG_PAGE    = "ARG_PAGE";
    String ARG_SERVER  = "ARG_SERVER";
    String STATE_PAGE = "STATE_PAGE";

    interface View extends BaseView<Presenter> {
        void showLostServerConnectionMessage();
        void closeView();
        void updatePage(OHLinkedPage page);
        void setWidgets(List<WidgetFactory.IWidgetHolder> widgets);
    }

    interface Presenter extends BasePresenter {

    }
}
