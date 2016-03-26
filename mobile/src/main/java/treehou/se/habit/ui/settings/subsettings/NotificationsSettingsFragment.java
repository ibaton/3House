package treehou.se.habit.ui.settings.subsettings;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import io.realm.Realm;
import treehou.se.habit.R;
import treehou.se.habit.core.db.settings.NotificationSettingsDB;

public class NotificationsSettingsFragment extends Fragment {

    private Realm realm;

    public static NotificationsSettingsFragment newInstance() {
        NotificationsSettingsFragment fragment = new NotificationsSettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public NotificationsSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        realm.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);

        final NotificationSettingsDB settings = NotificationSettingsDB.loadGlobal(realm);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(R.string.settings_notification);
        }

        CheckBox cbxNotificationToSpeech = (CheckBox) rootView.findViewById(R.id.cbx_notification_to_speech);
        cbxNotificationToSpeech.setChecked(settings.notificationToSpeech());
        cbxNotificationToSpeech.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                realm.beginTransaction();
                settings.setNotificationToSpeech(isChecked);
                realm.commitTransaction();
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }


}
