package treehou.se.habit.ui.widget

import android.support.v7.app.AlertDialog
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import se.treehou.ng.ohcommunicator.connector.models.OHItem
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.services.IServerHandler
import treehou.se.habit.R
import treehou.se.habit.ui.adapter.WidgetAdapter
import treehou.se.habit.util.logging.Logger
import javax.inject.Inject

class WidgetTextFactory @Inject constructor() : WidgetFactory {

    @Inject lateinit var logger: Logger
    @Inject lateinit var server: OHServer
    @Inject lateinit var page: OHLinkedPage
    @Inject lateinit var serverHandler: IServerHandler

    override fun createViewHolder(parent: ViewGroup): WidgetAdapter.WidgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.widget_text, parent, false)
        return SwitchWidgetViewHolder(view)
    }

    inner class SwitchWidgetViewHolder(view: View) : WidgetBaseHolder(view, server, page) {

        override fun bind(itemWidget: WidgetAdapter.WidgetItem) {
            super.bind(itemWidget)

            val widgetItem: OHItem? = widget.item
            if (widgetItem?.type == OHItem.TYPE_STRING && widgetItem.stateDescription?.isReadOnly != true) {
                itemView.setOnLongClickListener {
                    showInputDialog(widgetItem)
                    true
                }
            } else {
                itemView.setOnClickListener(null)
            }
        }

        private fun showInputDialog(item: OHItem) {
            val inputView = LayoutInflater.from(itemView.context).inflate(R.layout.dialog_input_text, null)
            val input = inputView.findViewById<EditText>(R.id.commandText)
            input.setText(item.state)

            if (item.type == OHItem.TYPE_STRING) {
                input.inputType = InputType.TYPE_CLASS_TEXT
            } else if (item.type == OHItem.TYPE_NUMBER) {
                input.inputType = InputType.TYPE_CLASS_NUMBER
            }

            AlertDialog.Builder(itemView.context)
                    .setTitle(itemView.context.getString(R.string.send_text_command))
                    .setPositiveButton(itemView.context.getString(R.string.ok)) { _, _ ->
                        val text = input.text.toString()
                        serverHandler.sendCommand(item.name, text)
                    }
                    .setNegativeButton(itemView.context.getString(R.string.cancel)) { dialog, which -> dialog.cancel() }
                    .setView(inputView)
                    .show()
        }
    }

    companion object {
        const val TAG = "WidgetSwitchFactory"
    }
}