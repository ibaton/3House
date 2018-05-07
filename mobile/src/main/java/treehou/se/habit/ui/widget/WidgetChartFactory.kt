package treehou.se.habit.ui.widget

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHWidget
import se.treehou.ng.ohcommunicator.services.Connector
import se.treehou.ng.ohcommunicator.services.IServerHandler
import se.treehou.ng.ohcommunicator.util.ConnectorUtil
import treehou.se.habit.R
import treehou.se.habit.connector.Communicator
import treehou.se.habit.ui.adapter.WidgetAdapter
import treehou.se.habit.util.logging.Logger
import javax.inject.Inject

class WidgetChartFactory @Inject constructor() : WidgetFactory {

    @Inject lateinit var logger: Logger
    @Inject lateinit var server: OHServer
    @Inject lateinit var page: OHLinkedPage
    @Inject lateinit var serverHandler: IServerHandler

    override fun createViewHolder(parent: ViewGroup): WidgetAdapter.WidgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.widget_chart, parent, false)
        return ChartWidgetViewHolder(view)
    }

    inner class ChartWidgetViewHolder(view: View) : WidgetBaseHolder(view, server, page) {

        private val chart: ImageView = view as ImageView

        override fun bind(itemWidget: WidgetAdapter.WidgetItem) {
            super.bind(itemWidget)

            try {
                val url = Connector.ServerHandler.getUrl(context, server)
                val imageUrl = Uri.parse(ConnectorUtil.buildChartRequestString(url, widget))
                val communicator = Communicator.instance(context)
                communicator.loadImage(server, imageUrl, chart, false)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update chart", e)
            }
        }
    }

    companion object {
        const val TAG = "WidgetSwitchFactory"
    }
}