package treehou.se.habit.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import io.realm.RealmResults;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.ui.homescreen.VoiceControlWidgetConfigureActivity;

public class ServerArrayAdapter extends ArrayAdapter<ServerDB> {
    public ServerArrayAdapter(VoiceControlWidgetConfigureActivity voiceControlWidgetConfigureActivity, RealmResults<ServerDB> servers) {
        super(voiceControlWidgetConfigureActivity, android.R.layout.simple_spinner_dropdown_item, servers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        textView.setText(getItem(position).getDisplayName());
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        textView.setText(getItem(position).getDisplayName());
        return textView;
    }
}
