package treehou.se.habit.ui.settings.subsettings;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.f2prateek.rx.preferences.Preference;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.trello.rxlifecycle.RxLifecycle;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.R;
import treehou.se.habit.module.ApplicationComponent;
import treehou.se.habit.ui.BaseFragment;
import treehou.se.habit.util.Settings;

public class GeneralSettingsFragment extends BaseFragment {

    private static final String TAG = GeneralSettingsFragment.class.getSimpleName();

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

    protected ApplicationComponent getApplicationComponent() {
        return ((HabitApplication) getContext().getApplicationContext()).component();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings_general, container, false);
        CheckBox cbxAutoLoadSitemap = (CheckBox) rootView.findViewById(R.id.cbx_open_last_sitemap);
        CheckBox cbxShowSitemapInMenu = (CheckBox) rootView.findViewById(R.id.cbx_show_sitemap_menu);
        CheckBox cbxFullscreen = (CheckBox) rootView.findViewById(R.id.cbx_show_fullscreen);
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

        Preference<Boolean> settingsShowSitemapInMenuRx = settings.getShowSitemapsInMenuRx();
        settingsShowSitemapInMenuRx.asObservable()
                .compose(RxLifecycle.bindFragment(this.lifecycle()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(RxCompoundButton.checked(cbxShowSitemapInMenu));
        RxCompoundButton.checkedChanges(cbxShowSitemapInMenu)
                .compose(RxLifecycle.bindFragment(this.lifecycle()))
                .skip(1)
                .subscribe(settingsShowSitemapInMenuRx.asAction());

        Preference<Boolean> fullscreenRx = settings.getFullscreenRx();
        fullscreenRx.asObservable()
                .compose(RxLifecycle.bindFragment(this.lifecycle()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(RxCompoundButton.checked(cbxFullscreen));
        RxCompoundButton.checkedChanges(cbxFullscreen)
                .compose(RxLifecycle.bindFragment(this.lifecycle()))
                .skip(1)
                .subscribe(fullscreenRx.asAction());
        cbxFullscreen.setVisibility(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? View.VISIBLE : View.GONE);

        Spinner spinnerThemes = (Spinner) rootView.findViewById(R.id.spr_themes);
        ThemeItem[] themeSpinner = new ThemeItem[] {
                new ThemeItem(Settings.Themes.THEME_DEFAULT, getString(R.string.treehouse)),
                new ThemeItem(Settings.Themes.THEME_HABDROID_LIGHT, getString(R.string.habdroid)),
                new ThemeItem(Settings.Themes.THEME_HABDROID_DARK, getString(R.string.dark))
        };
        ArrayAdapter<ThemeItem> themeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, themeSpinner);
        spinnerThemes.setAdapter(themeAdapter);

        AdapterView.OnItemSelectedListener themeListener = new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    if(settings.getTheme() != themeSpinner[position].theme){
                        settings.setTheme(themeSpinner[position].theme);
                        getActivity().recreate();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            };

        settings.getThemeRx()
                .subscribeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(integer -> {
                    for(int i=0; i < themeSpinner.length; i++){
                        if(themeSpinner[i].theme == settings.getTheme()){
                            spinnerThemes.setOnItemSelectedListener(null);
                            spinnerThemes.setSelection(i);
                            break;
                        }
                    }
                    spinnerThemes.setOnItemSelectedListener(themeListener);
                });

        // Inflate the layout for this fragment
        return rootView;
    }

    private class ThemeItem {

        public final int theme;
        public final String name;

        public ThemeItem(int theme, String name) {
            this.theme = theme;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
