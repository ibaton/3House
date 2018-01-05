package treehou.se.habit.ui.control.cells.config.cells

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.RemoteViews

import butterknife.BindView
import butterknife.ButterKnife
import treehou.se.habit.R
import treehou.se.habit.core.db.model.controller.CellDB
import treehou.se.habit.core.db.model.controller.ControllerDB
import treehou.se.habit.ui.control.CellFactory
import treehou.se.habit.ui.control.ControllerUtil

class DefaultConfigCellBuilder : CellFactory.CellBuilder {

    @BindView(R.id.img_icon_button) internal var imgView: ImageButton? = null

    override fun build(context: Context, controller: ControllerDB, cell: CellDB): View {

        val inflater = LayoutInflater.from(context)
        val rootView = inflater.inflate(R.layout.cell_conf_button, null)
        ButterKnife.bind(this, rootView)

        val pallete = ControllerUtil.generateColor(controller, cell)

        imgView!!.background.setColorFilter(pallete[ControllerUtil.INDEX_BUTTON], PorterDuff.Mode.MULTIPLY)

        return rootView
    }

    override fun buildRemote(context: Context, controller: ControllerDB, cell: CellDB): RemoteViews? {
        return null
    }
}
