package treehou.se.habit.mvp;

public interface BaseView<T extends BasePresenter> {
    T getPresenter();
}