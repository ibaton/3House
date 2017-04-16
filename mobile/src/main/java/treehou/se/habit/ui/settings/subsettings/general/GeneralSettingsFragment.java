package treehou.se.habit.ui.settings.subsettings.general;


import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.jakewharton.rxbinding.widget.RxCompoundButton;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;
import treehou.se.habit.R;
import treehou.se.habit.module.HasActivitySubcomponentBuilders;
import treehou.se.habit.mvp.BaseDaggerFragment;
import treehou.se.habit.util.Settings;

public class GeneralSettingsFragment extends BaseDaggerFragment<GeneralSettingsContract.Presenter> implements GeneralSettingsContract.View {

    private static final String TAG = GeneralSettingsFragment.class.getSimpleName();

    @Inject GeneralSettingsContract.Presenter presenter;
    @Inject Settings settings;
    @Inject ThemeItem[] themes;

    @BindView(R.id.cbx_open_last_sitemap) CheckBox cbxAutoLoadSitemap;
    @BindView(R.id.cbx_show_sitemap_menu) CheckBox cbxShowSitemapInMenu;
    @BindView(R.id.cbx_show_fullscreen) CheckBox cbxFullscreen;

    private Unbinder unbinder;

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
    public GeneralSettingsContract.Presenter getPresenter() {
        return presenter;
    }

    @Override
    protected void injectMembers(HasActivitySubcomponentBuilders hasActivitySubcomponentBuilders) {
        ((GeneralSettingsComponent.Builder) hasActivitySubcomponentBuilders.getFragmentComponentBuilder(GeneralSettingsFragment.class))
                .fragmentModule(new GeneralSettingsModule(this))
                .build().injectMembers(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings_general, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if(actionBar != null) {
            actionBar.setTitle(R.string.settings_general);
        }

        cbxFullscreen.setVisibility(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? View.VISIBLE : View.GONE);

        Spinner spinnerThemes = (Spinner) rootView.findViewById(R.id.spr_themes);

        ArrayAdapter<ThemeItem> themeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, themes);
        spinnerThemes.setAdapter(themeAdapter);

        AdapterView.OnItemSelectedListener themeListener = new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    presenter.themeSelected(themes[position].theme);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            };

        settings.getThemeRx()
                .subscribeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(integer -> {
                    for(int i=0; i < themes.length; i++){
                        if(themes[i].theme == settings.getTheme()){
                            spinnerThemes.setOnItemSelectedListener(null);
                            spinnerThemes.setSelection(i);
                            break;
                        }
                    }
                    spinnerThemes.setOnItemSelectedListener(themeListener);
                });

        RxCompoundButton.checkedChanges(cbxShowSitemapInMenu)
                .compose(bindToLifecycle())
                .skip(1)
                .subscribe(show -> presenter.setShowSitemapsInMenu(show));

        RxCompoundButton.checkedChanges(cbxAutoLoadSitemap)
                .compose(bindToLifecycle())
                .skip(1)
                .subscribe(show -> presenter.setAutoLoadSitemap(show));

        RxCompoundButton.checkedChanges(cbxFullscreen)
                .compose(bindToLifecycle())
                .skip(1)
                .subscribe(show -> presenter.setFullscreen(show));

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void updateTheme(){
        getActivity().recreate();
    }

    @Override
    public void showAutoLoadSitemap(boolean show) {
        cbxAutoLoadSitemap.setChecked(show);
    }

    @Override
    public void showSitemapsInMenu(Boolean show) {

        cbxShowSitemapInMenu.setChecked(show);
    }

    @Override
    public void setFullscreen(boolean fullscreen) {
        cbxFullscreen.setChecked(fullscreen);
    }

}
