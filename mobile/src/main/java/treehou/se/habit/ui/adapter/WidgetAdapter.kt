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
import treehou.se.habit.util.isRollerShutter
import java.net.MalformedURLException
import javax.inject.Inject

class WidgetAdapter @Inject constructor() : RecyclerView.Adapter<WidgetAdapter.WidgetViewHolder>() {

    @Inject lateinit var widgetNullFactory: WidgetNullFactory
    @Inject lateinit var widgetSwitchFactory: WidgetSwitchFactory
    @Inject lateinit var widgetMultiSwitchFactory: WidgetMultiSwitchFactory
    @Inject lateinit var widgetButtonFactory: WidgetButtonFactory
    @Inject lateinit var widgetFrameFactory: WidgetFrameFactory
    @Inject lateinit var widgetTextFactory: WidgetTextFactory
    @Inject lateinit var widgetColorpickerFactory: WidgetColorpickerFactory
    @Inject lateinit var widgetSliderFactory: WidgetSliderFactory
    @Inject lateinit var widgetSetpointFactory: WidgetSetpointFactory
    @Inject lateinit var widgetRollerShutterFactory: WidgetRollerShutterFactory
    @Inject lateinit var widgetGroupFactory: WidgetGroupFactory
    @Inject lateinit var widgetSelectionFactory: WidgetSelectionFactory
    @Inject lateinit var widgetImageFactory: WidgetImageFactory
    @Inject lateinit var widgetVideoFactory: WidgetVideoFactory
    @Inject lateinit var widgetWebViewFactory: WidgetWebViewFactory
    @Inject lateinit var widgetChartFactory: WidgetChartFactory

    abstract class WidgetViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        abstract fun bind(widgetItem: WidgetItem)

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
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else {
                imgIcon.visibility = View.INVISIBLE
            }
        }
    }

    private var items: List<WidgetItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetViewHolder {

        return when (viewType) {
            ITEM_TYPE_SWITCH -> widgetSwitchFactory.createViewHolder(parent)
            ITEM_TYPE_SWITCH_PICKER -> widgetMultiSwitchFactory.createViewHolder(parent)
            ITEM_TYPE_SWITCH_BUTTON -> widgetButtonFactory.createViewHolder(parent)
            ITEM_TYPE_ROLLERSHUTTER -> widgetRollerShutterFactory.createViewHolder(parent)
            ITEM_TYPE_FRAME -> widgetFrameFactory.createViewHolder(parent)
            ITEM_TYPE_TEXT -> widgetTextFactory.createViewHolder(parent)
            ITEM_TYPE_COLORPICKER -> widgetColorpickerFactory.createViewHolder(parent)
            ITEM_TYPE_SLIDER -> widgetSliderFactory.createViewHolder(parent)
            ITEM_TYPE_SETPOINT -> widgetSetpointFactory.createViewHolder(parent)
            ITEM_TYPE_GROUP -> widgetGroupFactory.createViewHolder(parent)
            ITEM_TYPE_SELECTION -> widgetSelectionFactory.createViewHolder(parent)
            ITEM_TYPE_IMAGE -> widgetImageFactory.createViewHolder(parent)
            ITEM_TYPE_VIDEO -> widgetVideoFactory.createViewHolder(parent)
            ITEM_TYPE_CHART -> widgetChartFactory.createViewHolder(parent)
            ITEM_TYPE_WEB_VIEW -> widgetWebViewFactory.createViewHolder(parent)
            else -> widgetNullFactory.createViewHolder(parent)
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
        return when (item.widget.type) {
            OHWidget.WIDGET_TYPE_FRAME -> ITEM_TYPE_FRAME
            OHWidget.WIDGET_TYPE_TEXT -> ITEM_TYPE_TEXT
            OHWidget.WIDGET_TYPE_COLORPICKER -> ITEM_TYPE_COLORPICKER
            OHWidget.WIDGET_TYPE_SLIDER -> ITEM_TYPE_SLIDER
            OHWidget.WIDGET_TYPE_SETPOINT -> ITEM_TYPE_SETPOINT
            OHWidget.WIDGET_TYPE_GROUP -> ITEM_TYPE_GROUP
            OHWidget.WIDGET_TYPE_SELECTION -> ITEM_TYPE_SELECTION
            OHWidget.WIDGET_TYPE_IMAGE -> ITEM_TYPE_IMAGE
            OHWidget.WIDGET_TYPE_VIDEO -> ITEM_TYPE_VIDEO
            OHWidget.WIDGET_TYPE_WEB -> ITEM_TYPE_WEB_VIEW
            OHWidget.WIDGET_TYPE_CHART -> ITEM_TYPE_CHART
            OHWidget.WIDGET_TYPE_SWITCH -> {
                when {
                    item.widget.isRollerShutter() -> ITEM_TYPE_ROLLERSHUTTER
                    item.widget.mapping == null -> ITEM_TYPE_SWITCH
                    item.widget.mapping.isEmpty() -> ITEM_TYPE_SWITCH
                    item.widget.mapping.size == 1 -> ITEM_TYPE_SWITCH_BUTTON
                    else -> ITEM_TYPE_SWITCH_PICKER
                }
            }
            else -> ITEM_TYPE_NULL
        }
    }

    fun setWidgets(widgets: List<OHWidget>) {
        items = flatternWidgets(widgets)
        notifyDataSetChanged()
    }

    private fun flatternWidgets(widgets: List<OHWidget>): List<WidgetItem> {
        return widgets.flatMap { parent ->
            val childWidgets = parent.widget?.toList() ?: emptyList()
            listOf(parent, *childWidgets.toTypedArray())
                    .map { WidgetItem(it, parent) }
        }
    }

    fun removeAllWidgets() {
        items = listOf()
        notifyDataSetChanged()
    }

    data class WidgetItem(val widget: OHWidget, val parent: OHWidget? = null){
        fun hasParent() : Boolean {
            return parent != null
        }
    }

    companion object {
        const val ITEM_TYPE_NULL = 0
        const val ITEM_TYPE_SWITCH = 1
        const val ITEM_TYPE_SWITCH_PICKER = 2
        const val ITEM_TYPE_SWITCH_BUTTON = 3
        const val ITEM_TYPE_FRAME = 4
        const val ITEM_TYPE_TEXT = 5
        const val ITEM_TYPE_COLORPICKER = 6
        const val ITEM_TYPE_SLIDER = 7
        const val ITEM_TYPE_SETPOINT = 8
        const val ITEM_TYPE_ROLLERSHUTTER = 9
        const val ITEM_TYPE_GROUP = 10
        const val ITEM_TYPE_SELECTION = 11
        const val ITEM_TYPE_IMAGE = 12
        const val ITEM_TYPE_VIDEO = 13
        const val ITEM_TYPE_WEB_VIEW = 14
        const val ITEM_TYPE_CHART = 15
    }
}