package treehou.se.habit.ui.control.cells.config.cells

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
import treehou.se.habit.util.Util

class VoiceConfigCellBuilder : CellFactory.CellBuilder {

    override fun build(context: Context, container: ViewGroup, controller: ControllerDB, cell: CellDB): View {
        val inflater = LayoutInflater.from(context)
        val cellView = inflater.inflate(R.layout.cell_conf_button, container, false)
        val iconButton = cellView.findViewById<ImageButton>(R.id.iconButton)

        val realm = Realm.getDefaultInstance()
        val voiceCell = cell.getCellVoice()
        realm.close()

        val pallete = ControllerUtil.generateColor(controller, cell)

        cellView.setBackgroundColor(pallete[ControllerUtil.INDEX_BUTTON])

        val icon = Util.getIconDrawable(context, voiceCell?.icon)
        iconButton.background.setColorFilter(pallete[ControllerUtil.INDEX_BUTTON], PorterDuff.Mode.MULTIPLY)
        if (icon != null) {
            iconButton.setImageDrawable(icon)
        }

        return cellView
    }

    override fun buildRemote(context: Context, controller: ControllerDB, cell: CellDB): RemoteViews? {
        return null
    }

    companion object {

        private val TAG = "VoiceConfigCellBuilder"
    }
}
