package treehou.se.habit.ui.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import se.treehou.ng.ohcommunicator.connector.models.OHWidget
import treehou.se.habit.R
import treehou.se.habit.ui.adapter.WidgetAdapter
import treehou.se.habit.ui.view.WidgetTextView
import javax.inject.Inject

class WidgetFrameFactory @Inject constructor() : WidgetFactory {

    @Inject lateinit var context: Context

    override fun createViewHolder(parent: ViewGroup): WidgetAdapter.WidgetViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.widget_frame, parent, false)
        return SwitchWidgetViewHolder(view)
    }

    inner class SwitchWidgetViewHolder(view: View) : WidgetAdapter.WidgetViewHolder(view) {

        private val name: WidgetTextView = view.findViewById(R.id.widgetName)

        override fun bind(widget: OHWidget) {
            name.setText(widget.label, widget.labelColor)
        }
    }
}