package treehou.se.habit.ui.control.cells.builders

import android.content.Context
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RemoteViews
import io.realm.Realm
import treehou.se.habit.R
import treehou.se.habit.connector.Communicator
import treehou.se.habit.core.db.model.controller.CellDB
import treehou.se.habit.core.db.model.controller.ControllerDB
import treehou.se.habit.ui.control.CellFactory
import treehou.se.habit.ui.control.CommandService
import treehou.se.habit.ui.control.ControllerUtil
import treehou.se.habit.ui.util.ViewHelper
import treehou.se.habit.util.Util

class IncDecCellBuilder(private val communicator: Communicator) : CellFactory.CellBuilder {

    override fun build(context: Context, container: ViewGroup, controller: ControllerDB, cell: CellDB): View {
        val inflater = LayoutInflater.from(context)
        val cellView = inflater.inflate(R.layout.cell_button, container, false)
        val imgIcon = cellView.findViewById<ImageButton>(R.id.imgIcon)

        val realm = Realm.getDefaultInstance()
        val buttonCell = cell.getCellIncDec()

        val pallete = ControllerUtil.generateColor(controller, cell)
        imgIcon.background.setColorFilter(pallete[ControllerUtil.INDEX_BUTTON], PorterDuff.Mode.MULTIPLY)

        Log.d(TAG, "Build: Button icon " + buttonCell!!.icon)

        val icon = Util.getIconDrawable(context, buttonCell.icon)
        if (icon != null) {
            imgIcon.setImageDrawable(icon)
            imgIcon.setOnClickListener {
                val server = buttonCell.item!!.server
                communicator.incDec(server!!.toGeneric(), buttonCell.item!!.name, buttonCell.value, buttonCell.min, buttonCell.max)
            }
        }
        realm.close()

        return cellView
    }

    override fun buildRemote(context: Context, controller: ControllerDB, cell: CellDB): RemoteViews? {
        val realm = Realm.getDefaultInstance()
        val buttonCell = cell.getCellIncDec()

        val cellView = RemoteViews(context.packageName, R.layout.cell_button)

        val pallete = ControllerUtil.generateColor(controller, cell)
        ViewHelper.colorRemoteDrawable(cellView, R.id.imgIcon, pallete[ControllerUtil.INDEX_BUTTON])
        val icon = Util.getIconBitmap(context, buttonCell!!.icon)
        if (icon != null) {
            cellView.setImageViewBitmap(R.id.imgIcon, icon)
        }
        val intent = CommandService.getActionIncDec(context, buttonCell!!.min, buttonCell.max, buttonCell.value, buttonCell.item!!.id)
        val pendingIntent = CommandService.createCommand(context, (Math.random() * Integer.MAX_VALUE).toInt(), intent)
        cellView.setOnClickPendingIntent(R.id.imgIcon, pendingIntent)
        realm.close()

        return cellView
    }

    companion object {

        private val TAG = "IncDecCellBuilder"
    }
}
