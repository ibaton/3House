package treehou.se.habit.ui.control.config.cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;

import treehou.se.habit.R;
import treehou.se.habit.core.controller.Cell;
import treehou.se.habit.core.controller.ColorCell;
import treehou.se.habit.core.controller.Controller;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.control.CellFactory;

public class ColorConfigCellBuilder implements CellFactory.CellBuilder {

    private static final String TAG = "ColorConfigCellBuilder";

    public View build(Context context, Controller controller, Cell cell){

        ColorCell colorCell = cell.colorCell();

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
    public RemoteViews buildRemote(Context context, Controller controller, Cell cell) {
        return null;
    }
}
