package treehou.se.habit.ui.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHMapping
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.services.IServerHandler
import treehou.se.habit.R
import treehou.se.habit.ui.adapter.WidgetAdapter
import treehou.se.habit.util.logging.Logger
import javax.inject.Inject

class WidgetSelectionFactory @Inject constructor() : WidgetFactory {

    @Inject lateinit var logger: Logger
    @Inject lateinit var server: OHServer
    @Inject lateinit var page: OHLinkedPage
    @Inject lateinit var serverHandler: IServerHandler

    override fun createViewHolder(parent: ViewGroup): WidgetAdapter.WidgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.widget_selection, parent, false)
        return SelectionWidgetViewHolder(view)
    }

    inner class SelectionWidgetViewHolder(view: View) : WidgetBaseHolder(view, server, page) {

        private var lastPosition = -1
        private val selectorSpinner: Spinner = view.findViewById(R.id.selectorSpinner)

        override fun bind(itemWidget: WidgetAdapter.WidgetItem) {
            super.bind(itemWidget)
            updateSpinner()
        }

        private fun updateSpinner() {
            selectorSpinner.onItemSelectedListener = null
            val mappings = widget.mapping
            val mappingAdapter = ArrayAdapter<OHMapping>(context, R.layout.item_text, mappings)
            val itemName = widget.item?.name
            val itemState = widget.item?.state
            selectorSpinner.adapter = mappingAdapter
            if(itemState != null) {
                for (i in mappings.indices) {
                    if (mappings[i].command == itemState) {
                        selectorSpinner.setSelection(i)
                        lastPosition = i
                        break
                    }
                }
            }

            //TODO request value
            // Prevents rouge initial fire
            selectorSpinner.post({
                selectorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        if (itemName != null && position != lastPosition) {
                            val mapping = mappings[position]
                            serverHandler.sendCommand(itemName, mapping.command)
                            lastPosition = position
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
                mappingAdapter.notifyDataSetChanged()
            })

        }
    }

    companion object {
        const val TAG = "WidgetSwitchFactory"
    }
}