package treehou.se.habit.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHWidget
import treehou.se.habit.connector.Communicator
import treehou.se.habit.ui.widget.*
import java.net.MalformedURLException
import javax.inject.Inject

class WidgetAdapter @Inject constructor() : RecyclerView.Adapter<WidgetAdapter.WidgetViewHolder>() {

    @Inject lateinit var nullWidgetFactory: WidgetNullFactory
    @Inject lateinit var switchWidgetFactory: WidgetSwitchFactory
    @Inject lateinit var multiSwitchWidgetFactory: WidgetMultiSwitchFactory
    @Inject lateinit var frameWidgetFactory: WidgetFrameFactory
    @Inject lateinit var textWidgetFactory: WidgetTextFactory
    @Inject lateinit var colorPickerWidgetFactory: WidgetColorpickerFactory
    @Inject lateinit var sliderWidgetFactory: WidgetSliderFactory

    abstract class WidgetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(widget: OHWidget)

        /**
         * Load icon and populate image view with it.
         * @param widget the widget to load icon for.
         */
        fun loadIcon(imgIcon: ImageView, server: OHServer, page: OHLinkedPage, widget: OHWidget) {

            // TODO text is default value. Remove when fixed on server
            val ignoreDefault = "text".equals(widget.icon, ignoreCase = true)
            if (widget.iconPath != null && !ignoreDefault) {
                imgIcon.visibility = View.INVISIBLE
                try {
                    val imageUrl = (page.baseUrl + widget.iconPath).toUri()
                    val communicator = Communicator.instance(itemView.context)
                    communicator.loadImage(server, imageUrl, imgIcon, false)
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                }

            } else {
                imgIcon.visibility = View.INVISIBLE
            }
        }
    }

    private var items: List<OHWidget> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetViewHolder {

        return when (viewType) {
            ITEM_TYPE_SWITCH -> switchWidgetFactory.createViewHolder(parent)
            ITEM_TYPE_SWITCH_PICKER -> multiSwitchWidgetFactory.createViewHolder(parent)
            ITEM_TYPE_FRAME -> frameWidgetFactory.createViewHolder(parent)
            ITEM_TYPE_TEXT -> textWidgetFactory.createViewHolder(parent)
            ITEM_TYPE_COLORPICKER -> colorPickerWidgetFactory.createViewHolder(parent)
            ITEM_TYPE_SLIDER -> sliderWidgetFactory.createViewHolder(parent)
            else -> nullWidgetFactory.createViewHolder(parent)
        }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: WidgetViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return when (item.type) {
            OHWidget.WIDGET_TYPE_FRAME -> ITEM_TYPE_FRAME
            OHWidget.WIDGET_TYPE_TEXT -> ITEM_TYPE_TEXT
            OHWidget.WIDGET_TYPE_COLORPICKER -> ITEM_TYPE_COLORPICKER
            OHWidget.WIDGET_TYPE_SLIDER -> ITEM_TYPE_SLIDER
            OHWidget.WIDGET_TYPE_SWITCH -> {
                if (item.mapping.isEmpty()) {
                    ITEM_TYPE_SWITCH
                } else {
                    ITEM_TYPE_SWITCH_PICKER
                }
            }
            else -> ITEM_TYPE_NULL
        }
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
        val ITEM_TYPE_SWITCH_PICKER = 2
        val ITEM_TYPE_FRAME = 3
        val ITEM_TYPE_TEXT = 4
        val ITEM_TYPE_COLORPICKER = 5
        val ITEM_TYPE_SLIDER = 6
    }
}