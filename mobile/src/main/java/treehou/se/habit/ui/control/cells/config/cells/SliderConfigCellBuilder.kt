package treehou.se.habit.ui.control.cells.config.cells

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RemoteViews
import android.widget.SeekBar

import butterknife.BindView
import butterknife.ButterKnife
import io.realm.Realm
import treehou.se.habit.R
import treehou.se.habit.core.db.model.controller.CellDB
import treehou.se.habit.core.db.model.controller.ControllerDB
import treehou.se.habit.core.db.model.controller.SliderCellDB
import treehou.se.habit.util.Util
import treehou.se.habit.ui.control.CellFactory
import treehou.se.habit.ui.control.ControllerUtil

class SliderConfigCellBuilder : CellFactory.CellBuilder {

    @BindView(R.id.viw_background) lateinit var viwBackground: View
    @BindView(R.id.sbr_value) lateinit var sbrValue: SeekBar
    @BindView(R.id.img_icon) lateinit var imgIcon: ImageView

    override fun build(context: Context, controller: ControllerDB, cell: CellDB): View {
        val inflater = LayoutInflater.from(context)
        val cellView = inflater.inflate(R.layout.cell_conf_slider, null)
        ButterKnife.bind(this, cellView)

        val realm = Realm.getDefaultInstance()
        val numberCell = cell.cellSlider

        val pallete = ControllerUtil.generateColor(controller, cell)

        viwBackground.background.setColorFilter(pallete[ControllerUtil.INDEX_BUTTON], PorterDuff.Mode.MULTIPLY)

        val icon = Util.getIconDrawable(context, numberCell.icon)
        if (icon != null) {
            imgIcon.setImageDrawable(icon)
        }
        realm.close()

        return cellView
    }


    override fun buildRemote(context: Context, controller: ControllerDB, cell: CellDB): RemoteViews? {
        return null
    }

    companion object {

        private val TAG = "SliderConfigCellBuilder"
    }
}
