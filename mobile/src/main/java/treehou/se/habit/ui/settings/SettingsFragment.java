package treehou.se.habit.ui.settings;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.mikepenz.aboutlibraries.LibsBuilder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import treehou.se.habit.R;
import treehou.se.habit.module.HasActivitySubcomponentBuilders;
import treehou.se.habit.mvp.BaseDaggerFragment;
import treehou.se.habit.ui.adapter.ImageAdapter;
import treehou.se.habit.ui.adapter.ImageItem;
import treehou.se.habit.ui.settings.subsettings.general.GeneralSettingsFragment;
import treehou.se.habit.ui.settings.subsettings.wiget.WidgetSettingsFragment;
import treehou.se.habit.util.IntentHelper;

public class SettingsFragment extends BaseDaggerFragment<SettingsContract.Presenter> implements SettingsContract.View {

    private Unbinder unbinder;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef ({
        SettingsItems.ITEM_WIDGETS,
        SettingsItems.ITEM_GENERAL,
        SettingsItems.ITEM_CUSTOM_WIDGETS,
        SettingsItems.ITEM_LICENSES,
        SettingsItems.ITEM_TRANSLATE})
    public @interface SettingsItems {
        int ITEM_WIDGETS = 1;
        int ITEM_GENERAL = 2;
        int ITEM_CUSTOM_WIDGETS = 3;
        int ITEM_LICENSES = 4;
        int ITEM_TRANSLATE = 5;
    }

    private ActionBar actionBar;

    /**
     * The fragment's ListView/GridView.
     */
    @BindView(android.R.id.list) AbsListView mListView;

    @Inject SettingsContract.Presenter presenter;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<ImageItem> items = new ArrayList<>();
        items.add(new ImageItem(SettingsItems.ITEM_WIDGETS, getString(R.string.settings_widgets), R.drawable.ic_item_settings_widget));
        items.add(new ImageItem(SettingsItems.ITEM_GENERAL, getString(R.string.settings_general), R.drawable.ic_item_notification));
        items.add(new ImageItem(SettingsItems.ITEM_LICENSES, getString(R.string.open_source_libraries), R.drawable.ic_license));
        items.add(new ImageItem(SettingsItems.ITEM_TRANSLATE, getString(R.string.help_translate), R.drawable.ic_language));

        mAdapter = new ImageAdapter(getActivity(), items);
    }

    @Override
    public void showWidgetSettings() {
        Fragment fragment = WidgetSettingsFragment.Companion.newInstance();
        openPage(fragment);
    }

    @Override
    public void showGeneralSettings() {
        Fragment fragment = GeneralSettingsFragment.Companion.newInstance();
        openPage(fragment);
    }

    @Override
    public void showLicense() {
        actionBar.setTitle(R.string.open_source_libraries);
        Fragment fragment = new LibsBuilder().supportFragment();
        openPage(fragment);
    }

    @Override
    public void showTranslatePage() {
        openTranslationSite();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        unbinder = ButterKnife.bind(this, view);

        // Set the adapter
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(optionsSelectListener);

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) actionBar.setTitle(R.string.settings);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    AdapterView.OnItemClickListener optionsSelectListener = (parent, view, position, id) -> {
        ImageItem item = (ImageItem) parent.getItemAtPosition(position);

        switch (item.getId()) {
            case SettingsItems.ITEM_WIDGETS:
                presenter.openWidgetSettings();
                break;
            case SettingsItems.ITEM_GENERAL:
                presenter.openGeneralSettings();
                break;
            case SettingsItems.ITEM_LICENSES :
                presenter.openLicense();
                break;
            case SettingsItems.ITEM_TRANSLATE :
                presenter.openTranslatePage();
                break;
        }
    };

    private void openPage(Fragment fragment){
        if(fragment != null){
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.page_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public SettingsContract.Presenter getPresenter() {
        return presenter;
    }

    @Override
    protected void injectMembers(HasActivitySubcomponentBuilders hasActivitySubcomponentBuilders) {
        ((SettingsComponent.Builder) hasActivitySubcomponentBuilders.getFragmentComponentBuilder(SettingsFragment.class))
                .fragmentModule(new SettingsModule(this))
                .build().injectMembers(this);
    }

    /**
     * Opens translation site for project.
     */
    private void openTranslationSite(){
        startActivity(IntentHelper.INSTANCE.helpTranslateIntent());
    }
}
