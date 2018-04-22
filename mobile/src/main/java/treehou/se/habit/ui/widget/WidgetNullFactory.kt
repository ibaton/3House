package treehou.se.habit.ui.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import se.treehou.ng.ohcommunicator.connector.models.OHWidget
import treehou.se.habit.R
import treehou.se.habit.ui.adapter.WidgetAdapter
import javax.inject.Inject

class WidgetNullFactory @Inject constructor() : WidgetFactory {

    @Inject lateinit var context: Context

    override fun createViewHolder(parent: ViewGroup): WidgetAdapter.WidgetViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.widget_switch, parent, false)
        return SwitchWidgetViewHolder(view)
    }

    inner class SwitchWidgetViewHolder(view: View) : WidgetAdapter.WidgetViewHolder(view) {

        override fun bind(widget: OHWidget) {}
    }
}