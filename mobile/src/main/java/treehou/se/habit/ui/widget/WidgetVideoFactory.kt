package treehou.se.habit.ui.widget

import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.services.IServerHandler
import treehou.se.habit.R
import treehou.se.habit.ui.adapter.WidgetAdapter
import treehou.se.habit.util.logging.Logger
import javax.inject.Inject

class WidgetVideoFactory @Inject constructor() : WidgetFactory {

    @Inject lateinit var logger: Logger
    @Inject lateinit var server: OHServer
    @Inject lateinit var page: OHLinkedPage
    @Inject lateinit var serverHandler: IServerHandler

    override fun createViewHolder(parent: ViewGroup): WidgetAdapter.WidgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.widget_video, parent, false)
        return VideoWidgetViewHolder(view)
    }

    inner class VideoWidgetViewHolder(view: View) : WidgetBaseHolder(view, server, page) {

        private val video: VideoView = view.findViewById(R.id.video)

        override fun bind(itemWidget: WidgetAdapter.WidgetItem) {
            super.bind(itemWidget)

            if (this.widget.url != widget.url) {
                setVideoUri(widget.url)
                launchVideo()
            }
        }

        private fun setVideoUri(uri: String) {
            if (!TextUtils.isEmpty(uri)) {
                video.setVideoURI(Uri.parse(uri))
            }
        }

        private fun launchVideo() {
            //Initializing the video player’s media controller.
            val controller = MediaController(context)
            video.setOnPreparedListener({ mp ->
                Log.d(TAG, "Load video widget on prepare")
                video.layoutParams.height = (mp.videoWidth.toFloat() / mp.videoHeight).toInt() * video.layoutParams.width
                video.requestLayout()
            })

            video.setMediaController(controller)
        }
    }

    companion object {
        const val TAG = "WidgetSwitchFactory"
    }
}