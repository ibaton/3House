package treehou.se.habit.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by ibaton on 2015-03-21.
 */
public class CustomWidgetListFragment extends Fragment {

    public static SitemapListFragment newInstance() {
        SitemapListFragment fragment = new SitemapListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
}
