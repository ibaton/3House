package treehou.se.habit.ui.widget

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import treehou.se.habit.R
import treehou.se.habit.ui.adapter.WidgetAdapter
import treehou.se.habit.util.logging.Logger
import javax.inject.Inject

class WidgetNullFactory @Inject constructor() : WidgetFactory {

    @Inject lateinit var logger: Logger
    @Inject lateinit var server: OHServer
    @Inject lateinit var page: OHLinkedPage

    override fun createViewHolder(parent: ViewGroup): WidgetAdapter.WidgetViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.widget_null, parent, false)
        return SwitchWidgetViewHolder(view)
    }

    inner class SwitchWidgetViewHolder(view: View) : WidgetAdapter.WidgetViewHolder(view) {

        val widgetName = view.findViewById<TextView>(R.id.widgetName)

        override fun bind(widgetItem: WidgetAdapter.WidgetItem) {
            val context = itemView.context
            val widget = widgetItem.widget
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                widgetName.text = Html.fromHtml(context.getString(R.string.missing_widget, widget.type, widget.item?.type
                        ?: ""), Html.FROM_HTML_MODE_COMPACT)
            } else {
                widgetName.text = Html.fromHtml(context.getString(R.string.missing_widget, widget.type, widget.item?.type
                        ?: ""))
            }
        }
    }
}