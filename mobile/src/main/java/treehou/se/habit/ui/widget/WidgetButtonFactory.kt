package treehou.se.habit.ui.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import se.treehou.ng.ohcommunicator.connector.models.OHItem
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHWidget
import se.treehou.ng.ohcommunicator.services.IServerHandler
import treehou.se.habit.R
import treehou.se.habit.connector.Constants
import treehou.se.habit.ui.adapter.WidgetAdapter
import treehou.se.habit.util.logging.Logger
import javax.inject.Inject

class WidgetButtonFactory @Inject constructor() : WidgetFactory {

    @Inject lateinit var logger: Logger
    @Inject lateinit var server: OHServer
    @Inject lateinit var page: OHLinkedPage
    @Inject lateinit var serverHandler: IServerHandler

    override fun createViewHolder(parent: ViewGroup): WidgetAdapter.WidgetViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.widget_switch_button, parent, false)
        return SwitchWidgetViewHolder(view)
    }

    inner class SwitchWidgetViewHolder(view: View) : WidgetBaseHolder(view, server, page) {

        private val buttonView: Button = view.findViewById(R.id.widgetButton)

        init {
            setupClickListener()
        }

        private fun setupClickListener() {
            buttonView.setOnClickListener {
                val mapSingle = widget.mapping[0]
                logger.d(TAG, "${widget.label} $mapSingle")
                val item: OHItem? = widget.item
                if (item != null && item.stateDescription?.isReadOnly != true) {
                    serverHandler.sendCommand(item.name, mapSingle.command)
                }
            }
        }

        override fun bind(itemWidget: WidgetAdapter.WidgetItem) {
            super.bind(itemWidget)
            buttonView.text = widget.mapping[0].label
        }
    }

    companion object {
        const val TAG = "WidgetButtonFactory"
    }
}