package treehou.se.habit.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import se.treehou.ng.ohcommunicator.connector.models.OHWidget
import treehou.se.habit.ui.widget.WidgetNullFactory
import treehou.se.habit.ui.widget.WidgetSwitchFactory
import javax.inject.Inject

class WidgetAdapter @Inject constructor() : RecyclerView.Adapter<WidgetAdapter.WidgetViewHolder>() {

    @Inject lateinit var nullWidgetFactory: WidgetNullFactory
    @Inject lateinit var switchWidgetFactory: WidgetSwitchFactory

    abstract class WidgetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(widget: OHWidget)
    }

    private var items: List<OHWidget> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetViewHolder {

        return when (viewType) {
            ITEM_TYPE_SWITCH -> {
                switchWidgetFactory.createViewHolder(parent)
            }
            else -> {
                nullWidgetFactory.createViewHolder(parent)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: WidgetViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setWidgets(widgets: List<OHWidget>) {
        items = flatternWidgets(widgets)
        notifyDataSetChanged()
    }

    fun flatternWidgets(widgets: List<OHWidget>): List<OHWidget> {
        return widgets.flatMap {
            val childWidgets = if (it.widget != null) it.widget.toList() else emptyList()
            listOf(it, *childWidgets.toTypedArray())
        }
    }

    fun removeAllWidgets() {
        items = listOf()
        notifyDataSetChanged()
    }

    companion object {
        val ITEM_TYPE_NULL = 0
        val ITEM_TYPE_SWITCH = 1
    }
}