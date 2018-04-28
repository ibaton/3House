package treehou.se.habit.ui.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHWidget
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

    inner class SwitchWidgetViewHolder(view: View) : WidgetBaseHolder(view, server, page)
}