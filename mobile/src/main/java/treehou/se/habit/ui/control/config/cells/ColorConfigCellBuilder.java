package treehou.se.habit.ui.control.config.cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;

import treehou.se.habit.R;
import treehou.se.habit.core.controller.Cell;
import treehou.se.habit.core.controller.ColorCell;
import treehou.se.habit.core.controller.Controller;
import treehou.se.habit.ui.Util;
import treehou.se.habit.ui.control.CellFactory;
import treehou.se.habit.ui.control.Icon;

/**
 * Created by ibaton on 2014-11-08.
 */
public class ColorConfigCellBuilder implements CellFactory.CellBuilder {

    private static final String TAG = "SwitchConfigCellBuilder";

    public View build(Context context, Controller controller, Cell cell){

        ColorCell colorCell = cell.colorCell();

        LayoutInflater inflater = LayoutInflater.from(context);
        View cellView = inflater.inflate(R.layout.cell_conf_button, null);

        int iconId = colorCell.getIcon();
        int[] iconIds = context.getResources().getIntArray(R.array.cell_icons_values);

        for (int iconId1 : iconIds) {
            if (iconId1 == iconId) {
                break;
            }
        }
        Icon icon = Util.getIcon(context, colorCell.getIcon());
        ImageView imgIcon = (ImageView) cellView.findViewById(R.id.img_icon);
        if(icon != null) {
            imgIcon.setImageResource(icon.getResource());
        }
        imgIcon.getBackground().setColorFilter(cell.getColor(), PorterDuff.Mode.MULTIPLY);

        return cellView;
    }

    @Override
    public RemoteViews buildRemote(Context context, Controller controller, Cell cell) {
        return null;
    }
}
