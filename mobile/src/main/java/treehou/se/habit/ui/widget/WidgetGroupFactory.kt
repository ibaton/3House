package treehou.se.habit.ui.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHWidget
import treehou.se.habit.R
import treehou.se.habit.ui.adapter.WidgetAdapter
import treehou.se.habit.util.logging.Logger
import javax.inject.Inject

class WidgetGroupFactory @Inject constructor() : WidgetFactory {

    @Inject lateinit var logger: Logger
    @Inject lateinit var server: OHServer
    @Inject lateinit var page: OHLinkedPage

    override fun createViewHolder(parent: ViewGroup): WidgetAdapter.WidgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.widget_text, parent, false)
        return GroupWidgetViewHolder(view)
    }

    inner class GroupWidgetViewHolder(view: View) : WidgetBaseHolder(view, server, page)

    companion object {
        const val TAG = "WidgetSwitchFactory"
    }
}