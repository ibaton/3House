package treehou.se.habit.ui.widget

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import io.realm.Realm
import se.treehou.ng.ohcommunicator.connector.models.OHItem
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHWidget
import se.treehou.ng.ohcommunicator.services.IServerHandler
import treehou.se.habit.R
import treehou.se.habit.connector.Constants
import treehou.se.habit.core.db.settings.WidgetSettingsDB
import treehou.se.habit.ui.adapter.WidgetAdapter
import treehou.se.habit.ui.view.WidgetTextView
import treehou.se.habit.util.Util
import treehou.se.habit.util.logging.Logger
import javax.inject.Inject

class WidgetMultiSwitchFactory @Inject constructor() : WidgetFactory {

    @Inject lateinit var logger: Logger
    @Inject lateinit var server: OHServer
    @Inject lateinit var page: OHLinkedPage
    @Inject lateinit var serverHandler: IServerHandler

    override fun createViewHolder(parent: ViewGroup): WidgetAdapter.WidgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.widget_multi_switch, parent, false)
        return MultiSwitchWidgetViewHolder(view)
    }

    inner class MultiSwitchWidgetViewHolder(view: View) : WidgetBaseHolder(view, server, page) {

        private val widgetButtons: RadioGroup = view.findViewById(R.id.widgetButtons)

        override fun bind(itemWidget: WidgetAdapter.WidgetItem) {
            super.bind(itemWidget)

            widgetButtons.removeAllViews()
            val layoutInflater = LayoutInflater.from(itemView.context)
            for (mapping in widget.mapping) {
                val button= layoutInflater.inflate(R.layout.radio_button, widgetButtons, false) as RadioButton
                button.text = mapping.label
                button.id = button.hashCode()
                val item: OHItem? = widget.item
                if (item?.state == mapping.command) {
                    button.isChecked = true
                }

                button.setOnClickListener { v ->
                    if(item != null) {
                        serverHandler.sendCommand(item.name, mapping.command)
                    }
                }
                widgetButtons.addView(button)
            }
        }
    }

    companion object {
        const val TAG = "WidgetSwitchFactory"
    }
}