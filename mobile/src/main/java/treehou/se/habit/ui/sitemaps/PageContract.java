package treehou.se.habit.ui.sitemaps;


import treehou.se.habit.mvp.BasePresenter;
import treehou.se.habit.mvp.BaseView;

public interface PageContract {

    interface View extends BaseView<Presenter> {
    }

    interface Presenter extends BasePresenter {
    }
}
