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

import butterknife.Bind;
import butterknife.ButterKnife;
import treehou.se.habit.R;
import treehou.se.habit.ui.adapter.ImageAdapter;
import treehou.se.habit.ui.adapter.ImageItem;
import treehou.se.habit.ui.settings.subsettings.NotificationsSettingsFragment;
import treehou.se.habit.ui.settings.subsettings.WidgetSettingsFragment;
import treehou.se.habit.util.IntentHelper;

public class SettingsFragment extends Fragment {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef ({
        SettingsItems.ITEM_WIDGETS,
        SettingsItems.ITEM_NOTIFICATIONS,
        SettingsItems.ITEM_CUSTOM_WIDGETS,
        SettingsItems.ITEM_LICENSES,
        SettingsItems.ITEM_TRANSLATE})
    public @interface SettingsItems {
        int ITEM_WIDGETS = 1;
        int ITEM_NOTIFICATIONS = 2;
        int ITEM_CUSTOM_WIDGETS = 3;
        int ITEM_LICENSES = 4;
        int ITEM_TRANSLATE = 5;
    }

    /**
     * The fragment's ListView/GridView.
     */
    @Bind(android.R.id.list) AbsListView mListView;

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
        items.add(new ImageItem(SettingsItems.ITEM_NOTIFICATIONS, getString(R.string.settings_notification), R.drawable.ic_item_notification));
        items.add(new ImageItem(SettingsItems.ITEM_LICENSES, getString(R.string.open_source_libraries), R.drawable.ic_license));
        items.add(new ImageItem(SettingsItems.ITEM_TRANSLATE, getString(R.string.help_translate), R.drawable.ic_language));

        mAdapter = new ImageAdapter(getActivity(), items);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        ButterKnife.bind(this, view);

        // Set the adapter
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(optionsSelectListener);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) actionBar.setTitle(R.string.settings);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    AdapterView.OnItemClickListener optionsSelectListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ImageItem item = (ImageItem) parent.getItemAtPosition(position);

            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            Fragment fragment = null;
            switch (item.getId()) {
                case SettingsItems.ITEM_WIDGETS:
                    fragment = WidgetSettingsFragment.newInstance();
                    break;
                case SettingsItems.ITEM_NOTIFICATIONS :
                    fragment = NotificationsSettingsFragment.newInstance();
                    break;
                case SettingsItems.ITEM_LICENSES :
                    fragment = new LibsBuilder().supportFragment();
                    if(actionBar != null) actionBar.setTitle(R.string.open_source_libraries);
                    break;
                case SettingsItems.ITEM_TRANSLATE :
                    openTranslationSite();
                    return;
            }

            if(fragment != null){
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.page_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
    };

    /**
     * Opens translation site for project.
     */
    private void openTranslationSite(){
        startActivity(IntentHelper.helpTranslateIntent());
    }
}
