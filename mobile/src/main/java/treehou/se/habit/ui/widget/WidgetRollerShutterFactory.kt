package treehou.se.habit.ui.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHWidget
import se.treehou.ng.ohcommunicator.services.IServerHandler
import treehou.se.habit.R
import treehou.se.habit.connector.Constants
import treehou.se.habit.ui.adapter.WidgetAdapter
import treehou.se.habit.util.logging.Logger
import javax.inject.Inject

class WidgetRollerShutterFactory @Inject constructor() : WidgetFactory {

    @Inject lateinit var logger: Logger
    @Inject lateinit var server: OHServer
    @Inject lateinit var page: OHLinkedPage
    @Inject lateinit var serverHandler: IServerHandler

    override fun createViewHolder(parent: ViewGroup): WidgetAdapter.WidgetViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.widget_roller_shutter, parent, false)
        return RollerShutterWidgetViewHolder(view)
    }

    inner class RollerShutterWidgetViewHolder(view: View) : WidgetBaseHolder(view, server, page) {

        val rollershutterButtonUp: ImageView = itemView.findViewById(R.id.rollerShutterButtonUp)
        val rollershutterButtonStop: ImageView = itemView.findViewById(R.id.rollerShutterButtonStop)
        val rollershutterButtonDown: ImageView = itemView.findViewById(R.id.rollerShutterButtonDown)

        init {
            setupClickListener()
        }

        private fun setupClickListener() {
            rollershutterButtonUp.setOnClickListener {
                if (widget.item != null) {
                    serverHandler.sendCommand(widget.item.name, Constants.COMMAND_UP)
                }
            }

            rollershutterButtonStop.setOnClickListener {
                if (widget.item != null) {
                    serverHandler.sendCommand(widget.item.getName(), Constants.COMMAND_STOP)
                }
            }

            rollershutterButtonDown.setOnClickListener {
                if (widget.item != null) {
                    serverHandler.sendCommand(widget.item.getName(), Constants.COMMAND_DOWN)
                }
            }
        }
    }

    companion object {
        const val TAG = "WidgetSwitchFactory"
    }
}