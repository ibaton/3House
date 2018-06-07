package treehou.se.habit.ui.control.cells.builders

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RemoteViews
import io.realm.Realm
import treehou.se.habit.R
import treehou.se.habit.core.db.model.controller.CellDB
import treehou.se.habit.core.db.model.controller.ControllerDB
import treehou.se.habit.ui.control.CellFactory
import treehou.se.habit.ui.control.ControllerUtil
import treehou.se.habit.ui.util.ViewHelper

class EmptyCellBuilder : CellFactory.CellBuilder {

    override fun build(context: Context, container: ViewGroup, controller: ControllerDB, cell: CellDB): View {
        val inflater = LayoutInflater.from(context)
        val rootView = inflater.inflate(R.layout.cell_empty, container, false)

        val pallete = ControllerUtil.generateColor(controller, cell)

        rootView.setBackgroundColor(pallete[ControllerUtil.INDEX_BUTTON])
        val imgButton = rootView.findViewById<View>(R.id.imgIcon) as ImageButton
        imgButton.background.setColorFilter(pallete[ControllerUtil.INDEX_BUTTON], PorterDuff.Mode.MULTIPLY)

        return rootView
    }

    override fun buildRemote(context: Context, controller: ControllerDB, cell: CellDB): RemoteViews? {
        val realm = Realm.getDefaultInstance()

        val cellView = RemoteViews(context.packageName, R.layout.cell_empty)
        val pallete = ControllerUtil.generateColor(controller, cell)
        ViewHelper.colorRemoteDrawable(cellView, R.id.imgIcon, pallete[ControllerUtil.INDEX_BUTTON])
        realm.close()

        return cellView
    }
}
