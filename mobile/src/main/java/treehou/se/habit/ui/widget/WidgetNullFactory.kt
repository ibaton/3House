package treehou.se.habit.ui.widget

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import se.treehou.ng.ohcommunicator.connector.models.OHWidget
import treehou.se.habit.R
import treehou.se.habit.ui.adapter.WidgetAdapter
import javax.inject.Inject

class WidgetNullFactory @Inject constructor() : WidgetFactory {

    override fun createViewHolder(parent: ViewGroup): WidgetAdapter.WidgetViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.widget_null, parent, false)
        return SwitchWidgetViewHolder(view)
    }

    inner class SwitchWidgetViewHolder(view: View) : WidgetAdapter.WidgetViewHolder(view) {

        val widgetName = view.findViewById<TextView>(R.id.widgetName)

        override fun bind(widget: OHWidget) {
            val context = itemView.context
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                widgetName.text = Html.fromHtml(context.getString(R.string.missing_widget, widget.type, widget.item?.type ?: ""), Html.FROM_HTML_MODE_COMPACT)
            } else {
                widgetName.text = Html.fromHtml(context.getString(R.string.missing_widget, widget.type, widget.item?.type ?: ""))
            }
        }
    }
}