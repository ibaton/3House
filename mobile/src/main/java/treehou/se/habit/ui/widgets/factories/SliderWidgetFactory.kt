package treehou.se.habit.ui.widgets.factories

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar

import javax.inject.Inject

import io.realm.Realm
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHWidget
import se.treehou.ng.ohcommunicator.services.Connector
import se.treehou.ng.ohcommunicator.services.IServerHandler
import treehou.se.habit.R
import treehou.se.habit.core.db.settings.WidgetSettingsDB
import treehou.se.habit.ui.widgets.WidgetFactory
import treehou.se.habit.util.ConnectionFactory

class SliderWidgetFactory(private val connectionFactory: ConnectionFactory) : IWidgetFactory {

    override fun build(context: Context, factory: WidgetFactory, server: OHServer, page: OHLinkedPage, widget: OHWidget, parent: OHWidget): WidgetFactory.IWidgetHolder {
        return SliderWidgetHolder(context, factory, connectionFactory, server, page, widget, parent)
    }

    class SliderWidgetHolder(private val context: Context, factory: WidgetFactory, private val connectionFactory: ConnectionFactory, private val server: OHServer, page: OHLinkedPage, widget: OHWidget, parent: OHWidget) : WidgetFactory.IWidgetHolder {

        private val itemView: View
        /**
         * Returns the holders slider view.
         *
         * @return sliders.
         */
        val seekbarView: SeekBar
        private val baseHolder: BaseWidgetFactory.BaseWidgetHolder

        init {

            val realm = Realm.getDefaultInstance()
            val settings = WidgetSettingsDB.loadGlobal(realm)
            val flat = settings.isCompressedSlider
            realm.close()

            itemView = LayoutInflater.from(context).inflate(R.layout.item_widget_slider, null)
            seekbarView = itemView.findViewById(R.id.skb_dim)

            baseHolder = BaseWidgetFactory.BaseWidgetHolder.Builder(context, factory, server, page)
                    .setWidget(widget)
                    .setParent(parent)
                    .setFlat(flat)
                    .build()

            baseHolder.subView.addView(itemView)
            update(widget)
        }

        override fun getView(): View {
            return baseHolder.view
        }

        override fun update(widget: OHWidget?) {
            if (widget == null) {
                return
            }

            seekbarView.setOnSeekBarChangeListener(null)
            try {
                if (widget.item != null) {
                    val progress = java.lang.Float.valueOf(widget.item.state)!!
                    seekbarView.progress = progress.toInt()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update progress", e)
                seekbarView.progress = 0
            }

            seekbarView.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}

                override fun onStartTrackingTouch(seekBar: SeekBar) {}

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    if (widget.item != null) {
                        try {
                            val serverHandler = connectionFactory.createServerHandler(server, context)
                            serverHandler.sendCommand(widget.item.name, seekbarView.progress.toString())
                        } catch (e: Exception) {
                        }

                    }
                }
            })

            baseHolder.update(widget)
        }
    }

    companion object {

        private val TAG = "SliderWidgetFactory"
    }
}
