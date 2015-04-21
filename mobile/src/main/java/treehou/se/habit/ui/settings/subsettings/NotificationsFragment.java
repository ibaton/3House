package treehou.se.habit.ui.settings.subsettings;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import treehou.se.habit.R;
import treehou.se.habit.core.settings.NotificationSettings;

public class NotificationsFragment extends Fragment {


    // TODO: Rename and change types and number of parameters
    public static NotificationsFragment newInstance() {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public NotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);

        final NotificationSettings settings = NotificationSettings.loadGlobal(getActivity());

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.settings_notification);

        CheckBox cbxNotificationToSpeech = (CheckBox) rootView.findViewById(R.id.cbx_notification_to_speech);
        cbxNotificationToSpeech.setChecked(settings.notificationToSpeach());
        cbxNotificationToSpeech.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setNotificationToSpeach(isChecked);
                settings.save();
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }


}
