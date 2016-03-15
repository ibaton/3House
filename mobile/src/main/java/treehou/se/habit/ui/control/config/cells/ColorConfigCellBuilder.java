package treehou.se.habit.ui.control.config.cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;

import treehou.se.habit.R;
import treehou.se.habit.core.db.controller.CellDB;
import treehou.se.habit.core.db.controller.ColorCellDB;
import treehou.se.habit.core.db.controller.ControllerDB;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.control.CellFactory;

public class ColorConfigCellBuilder implements CellFactory.CellBuilder {

    private static final String TAG = "ColorConfigCellBuilder";

    public View build(Context context, ControllerDB controller, CellDB cell){

        ColorCellDB colorCell = null;//ColorCellDB.getCell(cell);

        LayoutInflater inflater = LayoutInflater.from(context);
        View cellView = inflater.inflate(R.layout.cell_conf_button, null);

        Drawable icon = Util.getIconDrawable(context, colorCell.getIcon());
        ImageView imgIcon = (ImageView) cellView.findViewById(R.id.img_icon);
        if(icon != null) {
            imgIcon.setImageDrawable(icon);
        }
        imgIcon.getBackground().setColorFilter(cell.getColor(), PorterDuff.Mode.MULTIPLY);

        return cellView;
    }

    @Override
    public RemoteViews buildRemote(Context context, ControllerDB controller, CellDB cell) {
        return null;
    }
}
