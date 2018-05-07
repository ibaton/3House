package treehou.se.habit.ui.widget

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import treehou.se.habit.R
import treehou.se.habit.ui.adapter.WidgetAdapter
import treehou.se.habit.ui.view.WidgetTextView
import javax.inject.Inject

class WidgetFrameFactory @Inject constructor() : WidgetFactory {

    @Inject lateinit var server: OHServer
    @Inject lateinit var page: OHLinkedPage

    override fun createViewHolder(parent: ViewGroup): WidgetAdapter.WidgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.widget_frame, parent, false)
        return SwitchWidgetViewHolder(view)
    }

    inner class SwitchWidgetViewHolder(view: View) : WidgetBaseHolder(view, server, page) {

        private val name: WidgetTextView? = view.findViewById(R.id.widgetName)

        override fun bind(itemWidget: WidgetAdapter.WidgetItem) {
            super.bind(itemWidget)
            name?.visibility = if(TextUtils.isEmpty(widget.label)) View.GONE else View.VISIBLE
        }
    }
}