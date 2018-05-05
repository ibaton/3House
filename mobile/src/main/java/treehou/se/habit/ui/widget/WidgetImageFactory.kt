package treehou.se.habit.ui.widget

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHWidget
import se.treehou.ng.ohcommunicator.services.IServerHandler
import treehou.se.habit.R
import treehou.se.habit.connector.Communicator
import treehou.se.habit.ui.adapter.WidgetAdapter
import treehou.se.habit.util.logging.Logger
import javax.inject.Inject

class WidgetImageFactory @Inject constructor() : WidgetFactory {

    @Inject lateinit var logger: Logger
    @Inject lateinit var server: OHServer
    @Inject lateinit var page: OHLinkedPage
    @Inject lateinit var serverHandler: IServerHandler

    override fun createViewHolder(parent: ViewGroup): WidgetAdapter.WidgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.widget_image, parent, false)
        return ImageWidgetViewHolder(view)
    }

    inner class ImageWidgetViewHolder(view: View) : WidgetBaseHolder(view, server, page) {

        private lateinit var widget: OHWidget
        private val image: ImageView = view.findViewById(R.id.image)

        override fun bind(widget: OHWidget) {
            super.bind(widget)
            this.widget = widget

            try {
                val imageUrl = Uri.parse(widget.url)
                val communicator = Communicator.instance(context)
                communicator.loadImage(server, imageUrl, image, false)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    companion object {
        const val TAG = "WidgetSwitchFactory"
    }
}