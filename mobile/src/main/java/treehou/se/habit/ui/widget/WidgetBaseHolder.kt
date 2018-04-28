package treehou.se.habit.ui.widget

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

abstract class WidgetBaseHolder constructor(view: View, val server: OHServer, val page: OHLinkedPage) : WidgetAdapter.WidgetViewHolder(view) {

    private val name: WidgetTextView = view.findViewById(R.id.widgetName)
    private val imgIcon: ImageView? = view.findViewById(R.id.widgetIcon)
    private val nextPageButton: ImageButton? = view.findViewById(R.id.nextPageButton)
    private lateinit var widget: OHWidget

    @CallSuper
    override fun bind(widget: OHWidget) {
        this.widget = widget

        name.setText(widget.label, widget.labelColor)
        if(imgIcon != null) {
            loadIcon(imgIcon, server, page, widget)
        }
        setupNextPage()
    }

    private fun setupNextPage() {
        if (widget.linkedPage != null) {
            nextPageButton?.visibility = View.VISIBLE
            nextPageButton?.setOnClickListener { EventBus.getDefault().post(widget.linkedPage) }
        } else {
            nextPageButton?.visibility = View.GONE
            nextPageButton?.setOnClickListener {}
        }
    }
}