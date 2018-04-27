package treehou.se.habit.ui.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import se.treehou.ng.ohcommunicator.connector.models.OHWidget
import treehou.se.habit.R
import treehou.se.habit.ui.adapter.WidgetAdapter
import javax.inject.Inject

class WidgetNullFactory @Inject constructor() : WidgetFactory {

    @Inject lateinit var context: Context

    override fun createViewHolder(parent: ViewGroup): WidgetAdapter.WidgetViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.widget_null, parent, false)
        return SwitchWidgetViewHolder(view)
    }

    inner class SwitchWidgetViewHolder(view: View) : WidgetAdapter.WidgetViewHolder(view) {

        val widgetName = view.findViewById<TextView>(R.id.widgetName)

        override fun bind(widget: OHWidget) {
            widgetName.text = context.getString(R.string.missing_widget, widget.type, widget.item?.type ?: "")
        }
    }
}