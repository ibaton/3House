package treehou.se.habit.ui.widget

import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.services.IServerHandler
import treehou.se.habit.R
import treehou.se.habit.ui.adapter.WidgetAdapter
import treehou.se.habit.util.getName
import treehou.se.habit.util.logging.Logger
import javax.inject.Inject

class WidgetSetpointFactory @Inject constructor() : WidgetFactory {

    @Inject lateinit var logger: Logger
    @Inject lateinit var server: OHServer
    @Inject lateinit var page: OHLinkedPage
    @Inject lateinit var serverHandler: IServerHandler

    override fun createViewHolder(parent: ViewGroup): WidgetAdapter.WidgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.widget_setpoint, parent, false)
        return SwitchWidgetViewHolder(view)
    }

    inner class SwitchWidgetViewHolder(view: View) : WidgetBaseHolder(view, server, page) {
        private val valueView: View = view.findViewById(R.id.widgetValueHolder)

        override fun bind(itemWidget: WidgetAdapter.WidgetItem) {
            super.bind(itemWidget)
            valueView.setOnClickListener { createPickerDialog() }
        }

        private fun createPickerDialog() {
            val minValue = widget.minValue.toFloat()
            val maxValue = widget.maxValue.toFloat()
            val stepSize = if (minValue == maxValue || widget.step < 0) 1f else widget.step

            val stepValues = mutableListOf<String>()
            var x = minValue
            while (x <= maxValue) {
                stepValues.add("%.1f".format(x))
                x += stepSize
            }

            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_numberpicker, null, false)
            val numberPicker: NumberPicker = dialogView.findViewById(R.id.numberpicker)

            numberPicker.minValue = 0
            numberPicker.maxValue = stepValues.size - 1
            numberPicker.displayedValues = stepValues.toTypedArray();

            val stateString = widget.item.state
            var stepIndex = stepValues.binarySearch(stateString, Comparator { t1: String, t2: String ->
                convertStringValueToFloat(t1).compareTo(convertStringValueToFloat(t2))
            })

            // Create wrap around
            if (stepIndex < 0) {
                stepIndex = (-(stepIndex + 1))
                stepIndex = Math.min(stepIndex, stepValues.size - 1)
            }
            numberPicker.value = stepIndex

            AlertDialog.Builder(context)
                    .setTitle(widget.getName())
                    .setView(dialogView)
                    .setPositiveButton(R.string.ok, object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            serverHandler.sendCommand(widget.item.name, stepValues[numberPicker.value])
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        }

        private fun convertStringValueToFloat(value: String): Float {
            return value.replace(",", ".").toFloatOrNull() ?: 0f
        }
    }

    companion object {
        const val TAG = "WidgetSwitchFactory"
    }
}