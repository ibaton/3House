package treehou.se.habit.ui.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHWidget
import treehou.se.habit.R
import treehou.se.habit.connector.Communicator
import treehou.se.habit.ui.adapter.WidgetAdapter
import java.net.MalformedURLException
import javax.inject.Inject

class WidgetSwitchFactory @Inject constructor() : WidgetFactory {

    @Inject lateinit var context: Context
    @Inject lateinit var server: OHServer
    @Inject lateinit var page: OHLinkedPage

    override fun createViewHolder(parent: ViewGroup): WidgetAdapter.WidgetViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.widget_switch, parent, false)
        return SwitchWidgetViewHolder(view)
    }

    inner class SwitchWidgetViewHolder(view: View) : WidgetAdapter.WidgetViewHolder(view) {

        val name: TextView = view.findViewById(R.id.lbl_widget_name)
        val iconHolder: View = view.findViewById(R.id.img_widget_icon_holder)
        val imgIcon: ImageView = view.findViewById(R.id.img_widget_icon)

        override fun bind(widget: OHWidget) {
            name.text = widget.label
            loadIcon(widget)
        }

        /**
         * Load icon and populate image view with it.
         * @param widget the widget to load icon for.
         */
        private fun loadIcon(widget: OHWidget) {

            // TODO text is default value. Remove when fixed on server
            val ignoreDefault = "text".equals(widget.icon, ignoreCase = true)
            if (widget.iconPath != null && !ignoreDefault) {
                imgIcon.visibility = View.INVISIBLE
                try {
                    val imageUrl = (page.baseUrl + widget.iconPath).toUri()
                    val communicator = Communicator.instance(context)
                    communicator.loadImage(server, imageUrl, imgIcon, false)
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                }

            } else {
                imgIcon.visibility = View.INVISIBLE
            }
        }
    }
}