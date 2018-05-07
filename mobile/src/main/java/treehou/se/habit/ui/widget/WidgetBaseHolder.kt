package treehou.se.habit.ui.widget

import android.content.Context
import android.os.Build
import android.support.annotation.CallSuper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import org.greenrobot.eventbus.EventBus
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHWidget
import treehou.se.habit.R
import treehou.se.habit.ui.adapter.WidgetAdapter
import treehou.se.habit.ui.view.WidgetTextView
import treehou.se.habit.util.dpToPixels
import treehou.se.habit.util.getColorAttr
import treehou.se.habit.util.getName


abstract class WidgetBaseHolder constructor(view: View, val server: OHServer, val page: OHLinkedPage) : WidgetAdapter.WidgetViewHolder(view) {

    private val name: WidgetTextView? = view.findViewById(R.id.widgetName)
    private val value: WidgetTextView? = view.findViewById(R.id.widgetValue)
    private val imgIcon: ImageView? = view.findViewById(R.id.widgetIcon)
    private val nextPageButton: ImageButton? = view.findViewById(R.id.nextPageButton)
    private lateinit var itemWidget: WidgetAdapter.WidgetItem

    @CallSuper
    override fun bind(itemWidget: WidgetAdapter.WidgetItem) {
        this.itemWidget = itemWidget

        val nameText = if (value != null) widget.getName() else widget.label
        val valueText = widget.item?.formatedValue ?: ""
        name?.setText(nameText, widget.labelColor)
        value?.setText("[$valueText]", widget.labelColor)

        if (imgIcon != null) {
            loadIcon(imgIcon, server, page, widget)
        }
        setupNextPage()
        nextPageButton?.visibility = if (widget.linkedPage != null) View.VISIBLE else View.GONE

        if (itemWidget.hasParent()) {
            itemView.setBackgroundResource(context.resources.getColorAttr(R.attr.widgetItemBackground, context.theme))
            setElevation(3f)
        } else {
            itemView.background = null
            setElevation(0f)
        }
    }

    private fun setElevation(elevationInDp: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            itemView.elevation = context.resources.dpToPixels(elevationInDp)
            itemView.invalidate()
        }
    }

    val widget: OHWidget
        get() = itemWidget.widget

    val context: Context
        get() = itemView.context

    init {
        setupNextPage()
    }

    private fun setupNextPage() {
        nextPageButton?.setOnClickListener {
            if (widget.linkedPage != null) {
                EventBus.getDefault().post(widget.linkedPage)
            }
        }
    }


}