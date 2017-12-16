package treehou.se.habit.mvp;


import android.os.Bundle;

import treehou.se.habit.BaseActivity;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.module.HasActivitySubcomponentBuilders;
import treehou.se.habit.ui.BaseFragment;

public abstract class BaseDaggerFragment<T extends BasePresenter> extends BaseFragment implements BaseView<T> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupComponent();
        getPresenter().load(getArguments(), savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPresenter().subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        getPresenter().unsubscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPresenter().unload();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getPresenter().save(outState);
    }

    protected void setupComponent() {
        injectMembers(HabitApplication.get(getContext()));
    }

    protected abstract void injectMembers(HasActivitySubcomponentBuilders hasActivitySubcomponentBuilders);
}
