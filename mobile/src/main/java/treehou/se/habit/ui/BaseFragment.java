package treehou.se.habit.ui;


import android.os.Bundle;
import android.view.Menu;

import com.trello.rxlifecycle2.components.support.RxFragment;

import javax.inject.Inject;

import io.realm.Realm;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.util.MenuTintUtils;
import treehou.se.habit.util.logging.Logger;

public class BaseFragment extends RxFragment {

    protected Realm realm;
    protected @Inject Logger logger;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((HabitApplication) getActivity().getApplication()).component().inject(this);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuTintUtils.tintAllIcons(getContext(), menu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
