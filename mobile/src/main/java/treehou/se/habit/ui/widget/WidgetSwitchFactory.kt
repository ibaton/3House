package treehou.se.habit.ui.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import se.treehou.ng.ohcommunicator.connector.models.OHItem
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHWidget
import se.treehou.ng.ohcommunicator.services.IServerHandler
import treehou.se.habit.R
import treehou.se.habit.connector.Constants
import treehou.se.habit.ui.adapter.WidgetAdapter
import treehou.se.habit.ui.view.WidgetTextView
import treehou.se.habit.util.logging.Logger
import javax.inject.Inject

class WidgetSwitchFactory @Inject constructor() : WidgetFactory {

    @Inject lateinit var logger: Logger
    @Inject lateinit var server: OHServer
    @Inject lateinit var page: OHLinkedPage
    @Inject lateinit var serverHandler: IServerHandler

    override fun createViewHolder(parent: ViewGroup): WidgetAdapter.WidgetViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.widget_switch, parent, false)
        return SwitchWidgetViewHolder(view)
    }

    inner class SwitchWidgetViewHolder(view: View) : WidgetBaseHolder(view, server, page) {

        private val switchView: Switch = view.findViewById(R.id.widgetSwitch)

        init {
            setupClickListener()
        }

        private fun setupClickListener() {
            itemView.setOnClickListener {
                val newState = !switchView.isChecked
                logger.d(TAG, "${widget.label} $newState")
                val item: OHItem? = widget.item
                if (item != null && item.stateDescription?.isReadOnly != true) {
                    switchView.isChecked = newState
                    serverHandler.sendCommand(item.name, if (newState) Constants.COMMAND_ON else Constants.COMMAND_OFF)
                }
            }
        }

        override fun bind(itemWidget: WidgetAdapter.WidgetItem) {
            super.bind(itemWidget)

            val item: OHItem? = widget.item

            if(item != null) {
                val isOn = item.state == Constants.COMMAND_ON
                switchView.isEnabled = item.stateDescription?.isReadOnly == false
                switchView.isChecked = isOn
            }
        }
    }

    companion object {
        const val TAG = "WidgetSwitchFactory"
    }
}