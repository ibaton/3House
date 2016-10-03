package treehou.se.habit.ui;


import android.view.Menu;

import com.trello.rxlifecycle.components.support.RxFragment;

import treehou.se.habit.util.MenuTintUtils;

public class BaseFragment extends RxFragment {

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuTintUtils.tintAllIcons(getContext(), menu);
    }
}
