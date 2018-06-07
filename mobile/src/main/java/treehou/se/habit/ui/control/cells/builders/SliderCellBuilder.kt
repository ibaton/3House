package treehou.se.habit.ui.control.cells.builders

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RemoteViews
import android.widget.SeekBar
import io.realm.Realm
import treehou.se.habit.R
import treehou.se.habit.core.db.model.controller.CellDB
import treehou.se.habit.core.db.model.controller.ControllerDB
import treehou.se.habit.ui.control.CellFactory
import treehou.se.habit.ui.control.ControllerUtil
import treehou.se.habit.ui.control.SliderActivity
import treehou.se.habit.ui.util.ViewHelper
import treehou.se.habit.util.ConnectionFactory
import treehou.se.habit.util.Util

class SliderCellBuilder(private val connectionFactory: ConnectionFactory) : CellFactory.CellBuilder {

    override fun build(context: Context, container: ViewGroup, controller: ControllerDB, cell: CellDB): View {
        val inflater = LayoutInflater.from(context)
        val cellView = inflater.inflate(R.layout.cell_slider, container, false)
        val imgIcon = cellView.findViewById<ImageView>(R.id.imgIcon)
        val sbrNumber = cellView.findViewById<SeekBar>(R.id.sbrNumber)
        val viwBackground = cellView.findViewById<View>(R.id.backgroundView)

        val realm = Realm.getDefaultInstance()
        val sliderCell = cell.getCellSlider()

        val item = sliderCell?.item
        val server = item?.server?.toGeneric()

        val pallete = ControllerUtil.generateColor(controller, cell)

        viwBackground.background.setColorFilter(pallete[ControllerUtil.INDEX_BUTTON], PorterDuff.Mode.MULTIPLY)

        imgIcon.setImageDrawable(Util.getIconDrawable(context, sliderCell!!.icon))
        sbrNumber.max = sliderCell.max
        sbrNumber.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (item == null || server == null) {
                    return
                }

                val serverHandler = connectionFactory.createServerHandler(server, context)
                serverHandler.sendCommand(item.name, "${seekBar.progress}")
            }
        })
        realm.close()

        return cellView
    }

    override fun buildRemote(context: Context, controller: ControllerDB, cell: CellDB): RemoteViews? {
        val realm = Realm.getDefaultInstance()
        val numberCell = cell.getCellSlider()

        val cellView = RemoteViews(context.packageName, R.layout.cell_button)

        val pallete = ControllerUtil.generateColor(controller, cell)
        ViewHelper.colorRemoteDrawable(cellView, R.id.imgIcon, pallete[ControllerUtil.INDEX_BUTTON])

        val icon = Util.getIconBitmap(context, numberCell!!.icon)
        if (icon != null) {
            cellView.setImageViewBitmap(R.id.imgIcon, icon)
        }

        //TODO give intent unique id
        val pendingIntent = PendingIntent.getActivity(context, (Math.random() * Integer.MAX_VALUE).toInt(), createSliderIntent(context, cell.id), PendingIntent.FLAG_UPDATE_CURRENT)
        cellView.setOnClickPendingIntent(R.id.imgIcon, pendingIntent)
        realm.close()

        return cellView
    }

    /**
     * Create a intent that can launch a slider view.
     *
     * @param context get context launching view
     * @param cellID the cell id
     * @return intent that can launch view.
     */
    private fun createSliderIntent(context: Context, cellID: Long): Intent {
        val intent = Intent(context.applicationContext, SliderActivity::class.java)
        intent.action = SliderActivity.ACTION_NUMBER
        intent.putExtra(SliderActivity.ARG_CELL, cellID)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or Intent.FLAG_ACTIVITY_NO_ANIMATION
        return intent
    }

    companion object {

        private val TAG = "SliderCellBuilder"
    }
}
