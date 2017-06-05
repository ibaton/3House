package treehou.se.habit.ui.control.cells.config.cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RemoteViews;

import butterknife.BindView;
import butterknife.ButterKnife;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.controller.CellDB;
import treehou.se.habit.core.db.model.controller.ControllerDB;
import treehou.se.habit.ui.control.CellFactory;
import treehou.se.habit.ui.control.ControllerUtil;

public class DefaultConfigCellBuilder implements CellFactory.CellBuilder {

    @BindView(R.id.img_icon_button) ImageButton imgView;

    public View build(Context context, ControllerDB controller, CellDB cell){

        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.cell_conf_button, null);
        ButterKnife.bind(this, rootView);

        int[] pallete = ControllerUtil.generateColor(controller, cell);

        imgView.getBackground().setColorFilter(pallete[ControllerUtil.INDEX_BUTTON], PorterDuff.Mode.MULTIPLY);

        return rootView;
    }

    @Override
    public RemoteViews buildRemote(Context context, ControllerDB controller, CellDB cell) {
        return null;
    }
}
