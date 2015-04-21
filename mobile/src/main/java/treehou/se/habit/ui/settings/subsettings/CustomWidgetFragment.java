package treehou.se.habit.ui.settings.subsettings;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import treehou.se.habit.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomWidgetFragment extends Fragment {

    public static CustomWidgetFragment newInstance() {
        CustomWidgetFragment fragment = new CustomWidgetFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public CustomWidgetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_custom_widget, container, false);
    }


}
