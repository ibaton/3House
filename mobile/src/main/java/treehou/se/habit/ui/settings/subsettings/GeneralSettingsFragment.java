package treehou.se.habit.ui.settings.subsettings;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.f2prateek.rx.preferences.Preference;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.components.support.RxFragment;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.R;
import treehou.se.habit.util.Settings;

public class GeneralSettingsFragment extends RxFragment {

    @Inject Settings settings;

    public static GeneralSettingsFragment newInstance() {
        GeneralSettingsFragment fragment = new GeneralSettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public GeneralSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getApplicationComponent().inject(this);
    }

    protected HabitApplication.ApplicationComponent getApplicationComponent() {
        return ((HabitApplication) getContext().getApplicationContext()).component();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings_general, container, false);
        CheckBox cbxAutoLoadSitemap = (CheckBox) rootView.findViewById(R.id.cbx_open_last_sitemap);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(R.string.settings_general);
        }

        Preference<Boolean> settingsAutoloadSitemapRx = settings.getAutoloadSitemapRx();
        settingsAutoloadSitemapRx.asObservable()
                .compose(RxLifecycle.bindFragment(this.lifecycle()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(RxCompoundButton.checked(cbxAutoLoadSitemap));
        RxCompoundButton.checkedChanges(cbxAutoLoadSitemap)
                .compose(RxLifecycle.bindFragment(this.lifecycle()))
                .skip(1)
                .subscribe(settingsAutoloadSitemapRx.asAction());

        // Inflate the layout for this fragment
        return rootView;
    }
}
