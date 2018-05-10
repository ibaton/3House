package treehou.se.habit.ui.widget

import android.view.ViewGroup
import treehou.se.habit.ui.adapter.WidgetAdapter

interface WidgetFactory {

    fun createViewHolder(parent: ViewGroup): WidgetAdapter.WidgetViewHolder
}