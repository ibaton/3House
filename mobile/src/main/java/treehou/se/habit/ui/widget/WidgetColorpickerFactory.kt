package treehou.se.habit.ui.widget

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import se.treehou.ng.ohcommunicator.connector.models.OHItem
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHWidget
import se.treehou.ng.ohcommunicator.services.IServerHandler
import se.treehou.ng.ohcommunicator.util.GsonHelper
import treehou.se.habit.R
import treehou.se.habit.connector.Constants
import treehou.se.habit.ui.adapter.WidgetAdapter
import treehou.se.habit.ui.colorpicker.ColorpickerActivity
import treehou.se.habit.util.logging.Logger
import javax.inject.Inject

class WidgetColorpickerFactory @Inject constructor() : WidgetFactory {

    @Inject lateinit var logger: Logger
    @Inject lateinit var server: OHServer
    @Inject lateinit var page: OHLinkedPage
    @Inject lateinit var serverHandler: IServerHandler

    override fun createViewHolder(parent: ViewGroup): WidgetAdapter.WidgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.widget_color_picker, parent, false)
        return ColorPickerWidgetViewHolder(view)
    }

    inner class ColorPickerWidgetViewHolder(view: View) : WidgetBaseHolder(view, server, page) {

        val incrementButton: ImageButton = view.findViewById(R.id.incrementButton)
        val decrementButton: ImageButton = view.findViewById(R.id.decrementButton)

        init {
            setupOpenColorPickerListener()
        }

        override fun bind(itemWidget: WidgetAdapter.WidgetItem) {
            super.bind(itemWidget)
            setupIncrementDecrementButtons()
        }

        private fun getColor(): Int {
             return if (widget.item != null && widget.item.state != null) {
                val sHSV = widget.item.state.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (sHSV.size == 3) {
                    val hSV = floatArrayOf(java.lang.Float.valueOf(sHSV[0])!!, java.lang.Float.valueOf(sHSV[1])!!, java.lang.Float.valueOf(sHSV[2])!!)
                    Color.HSVToColor(hSV)
                } else{
                    Color.TRANSPARENT
                }
            } else {
                Color.TRANSPARENT
            }
        }

        private fun setupOpenColorPickerListener(){
            val context = itemView.context
            itemView.setOnClickListener({
                val color = getColor()
                if (widget.item != null) {
                    val intent = Intent(context, ColorpickerActivity::class.java)
                    val gson = GsonHelper.createGsonBuilder()
                    intent.putExtra(ColorpickerActivity.EXTRA_SERVER, server.id)
                    intent.putExtra(ColorpickerActivity.EXTRA_WIDGET, gson.toJson(widget))
                    intent.putExtra(ColorpickerActivity.EXTRA_COLOR, color)

                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, context.getString(R.string.item_missing), Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "WidgetFactory doesn't contain item")
                }
            })
        }

        @SuppressLint("ClickableViewAccessibility")
        private fun setupIncrementDecrementButtons(){
            val item: OHItem? = widget.item;
            if(item != null){
                incrementButton.setOnTouchListener(HoldListener(
                        object : HoldListener.OnTickListener {
                            override fun onTick(tick: Int) {
                                if (tick > 0) {
                                    serverHandler.sendCommand(item.name, Constants.COMMAND_INCREMENT)
                                }
                            }
                        }
                        , object : HoldListener.OnReleaseListener {
                    override fun onRelease(tick: Int) {
                        if (tick <= 0) {
                            serverHandler.sendCommand(item.name, Constants.COMMAND_ON)
                        }
                    }
                }))
                decrementButton.setOnTouchListener(HoldListener(
                        object : HoldListener.OnTickListener {
                            override fun onTick(tick: Int) {
                                if (tick > 0) {
                                    serverHandler.sendCommand(item.name, Constants.COMMAND_DECREMENT)
                                }
                            }
                        }
                        , object : HoldListener.OnReleaseListener {
                    override fun onRelease(tick: Int) {
                        if (tick <= 0) {
                            serverHandler.sendCommand(item.name, Constants.COMMAND_OFF)
                        }
                    }
                }))
            } else {
                incrementButton.setOnTouchListener(null)
                decrementButton.setOnTouchListener(null)
            }
        }
    }

    companion object {
        const val TAG = "WidgetSwitchFactory"
    }
}