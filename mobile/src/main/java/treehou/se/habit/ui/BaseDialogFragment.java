package treehou.se.habit.ui;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.components.RxDialogFragment;
import com.trello.rxlifecycle2.components.support.RxFragment;

import javax.inject.Inject;

import io.realm.Realm;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.R;
import treehou.se.habit.util.MenuTintUtils;
import treehou.se.habit.util.logging.Logger;

public class BaseDialogFragment extends RxDialogFragment {

    protected Realm realm;
    protected @Inject Logger logger;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((HabitApplication) getActivity().getApplication()).component().inject(this);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
