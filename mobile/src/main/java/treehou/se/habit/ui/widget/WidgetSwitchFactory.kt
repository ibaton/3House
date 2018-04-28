package treehou.se.habit.ui.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
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

    inner class SwitchWidgetViewHolder(view: View) : WidgetAdapter.WidgetViewHolder(view) {

        private val name: WidgetTextView = view.findViewById(R.id.widgetName)
        private val switchView: Switch = view.findViewById(R.id.widgetSwitch)
        private val imgIcon: ImageView = view.findViewById(R.id.widgetIcon)
        private lateinit var widget: OHWidget

        init {
            setupClickListener()
        }

        private fun setupClickListener() {
            itemView.setOnClickListener({
                val newState = !switchView.isChecked
                logger.d(TAG, "${widget.label} $newState")
                if (widget.item?.stateDescription?.isReadOnly != true) {
                    switchView.isChecked = newState
                    serverHandler.sendCommand(widget.item.name, if (newState) Constants.COMMAND_ON else Constants.COMMAND_OFF)
                }
            })
        }

        override fun bind(widget: OHWidget) {
            this.widget = widget
            name.setText(widget.label, widget.labelColor)
            loadIcon(imgIcon, server, page, widget)

            val isOn = widget.item.state == Constants.COMMAND_ON
            switchView.isEnabled = widget.item.stateDescription?.isReadOnly == false
            switchView.isChecked = isOn
        }
    }

    companion object {
        const val TAG = "WidgetSwitchFactory"
    }
}