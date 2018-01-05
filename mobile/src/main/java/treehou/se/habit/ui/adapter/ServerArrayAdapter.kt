package treehou.se.habit.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import io.realm.RealmResults
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.ui.homescreen.VoiceControlWidgetConfigureActivity

class ServerArrayAdapter(voiceControlWidgetConfigureActivity: VoiceControlWidgetConfigureActivity, servers: RealmResults<ServerDB>) : ArrayAdapter<ServerDB>(voiceControlWidgetConfigureActivity, android.R.layout.simple_spinner_dropdown_item, servers) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val textView = super.getView(position, convertView, parent) as TextView
        textView.text = getItem(position)!!.displayName
        return textView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val textView = super.getView(position, convertView, parent) as TextView
        textView.text = getItem(position)!!.displayName
        return textView
    }
}
