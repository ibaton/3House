package treehou.se.habit.mvp;


import android.os.Bundle;

import treehou.se.habit.BaseActivity;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.module.HasActivitySubcomponentBuilders;
import treehou.se.habit.mvp.BasePresenter;
import treehou.se.habit.mvp.BaseView;

public abstract class BaseDaggerActivity<T extends BasePresenter> extends BaseActivity implements BaseView<T> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActivityComponent();
        getPresenter().load(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().subscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPresenter().unsubscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPresenter().unload();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getPresenter().save(outState);
    }

    protected void setupActivityComponent() {
        injectMembers(HabitApplication.get(this));
    }

    protected abstract void injectMembers(HasActivitySubcomponentBuilders hasActivitySubcomponentBuilders);
}
