package treehou.se.habit.ui.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.services.IServerHandler
import treehou.se.habit.R
import treehou.se.habit.ui.adapter.WidgetAdapter
import treehou.se.habit.util.logging.Logger
import javax.inject.Inject

class WidgetSliderFactory @Inject constructor() : WidgetFactory {

    @Inject lateinit var logger: Logger
    @Inject lateinit var server: OHServer
    @Inject lateinit var page: OHLinkedPage
    @Inject lateinit var serverHandler: IServerHandler

    override fun createViewHolder(parent: ViewGroup): WidgetAdapter.WidgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.widget_slider, parent, false)
        return SliderWidgetViewHolder(view)
    }

    inner class SliderWidgetViewHolder(view: View) : WidgetBaseHolder(view, server, page) {

        val seekBarView: SeekBar = view.findViewById(R.id.dimmerSeekBar)

        override fun bind(itemWidget: WidgetAdapter.WidgetItem) {
            super.bind(itemWidget)
            updateSeekBar()
        }

        private fun updateSeekBar() {
            seekBarView.setOnSeekBarChangeListener(null)
            try {
                if (widget.item != null) {
                    val progress = java.lang.Float.valueOf(widget.item.state)!!
                    seekBarView.progress = progress.toInt()
                }
            } catch (e: Exception) {
                logger.e(TAG, "Failed to update progress", e)
                seekBarView.progress = 0
            }

            seekBarView.setOnSeekBarChangeListener(seekBarChangeListener)
        }

        private val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (widget.item != null) {
                    serverHandler.sendCommand(widget.item.name, seekBarView.progress.toString())
                }
            }
        }

    }

    companion object {
        const val TAG = "WidgetSwitchFactory"
    }
}