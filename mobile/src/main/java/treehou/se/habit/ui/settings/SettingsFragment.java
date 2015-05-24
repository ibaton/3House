package treehou.se.habit.ui.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import java.util.ArrayList;

import treehou.se.habit.R;
import treehou.se.habit.ui.adapter.ImageAdapter;
import treehou.se.habit.ui.adapter.ImageItem;
import treehou.se.habit.ui.settings.subsettings.NotificationsSettingsFragment;
import treehou.se.habit.ui.settings.subsettings.SitemapSettingsFragment;
import treehou.se.habit.ui.settings.subsettings.WidgetSettingsFragment;

public class SettingsFragment extends Fragment {

    private static final int ITEM_WIDGETS = 1;
    private static final int ITEM_NOTIFICATIONS = 2;
    private static final int ITEM_CUSTOM_WIDGETS = 3;
    private static final int ITEM_SITEMAPS = 4;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

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
        items.add(new ImageItem(ITEM_WIDGETS, getActivity().getString(R.string.settings_widgets), R.drawable.ic_item_settings_widget));
        items.add(new ImageItem(ITEM_SITEMAPS, getActivity().getString(R.string.sitemaps), R.drawable.ic_icon_settings_sitemap));
        items.add(new ImageItem(ITEM_NOTIFICATIONS, getActivity().getString(R.string.settings_notification), R.drawable.ic_item_settings_notifications));

        mAdapter = new ImageAdapter(getActivity(), items);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(optionsSelectListener);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.settings);

        return view;
    }

    AdapterView.OnItemClickListener optionsSelectListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ImageItem item = (ImageItem) parent.getItemAtPosition(position);

            Fragment fragment = null;
            switch (item.getId()) {
                case ITEM_WIDGETS:
                    fragment = WidgetSettingsFragment.newInstance();
                    break;
                case ITEM_NOTIFICATIONS :
                    fragment = NotificationsSettingsFragment.newInstance();
                    break;
                case ITEM_SITEMAPS :
                    fragment = SitemapSettingsFragment.newInstance();
                    break;
            }

            if(fragment != null){
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.page_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
    };
}
