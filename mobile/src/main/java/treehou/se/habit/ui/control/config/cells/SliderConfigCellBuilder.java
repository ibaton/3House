package treehou.se.habit.ui.control.config.cells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;

import treehou.se.habit.R;
import treehou.se.habit.core.controller.Cell;
import treehou.se.habit.core.controller.Controller;
import treehou.se.habit.core.controller.SliderCell;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.control.CellFactory;
import treehou.se.habit.ui.control.ControllerUtil;

/**
 * Created by ibaton on 2014-11-08.
 */
public class SliderConfigCellBuilder implements CellFactory.CellBuilder {

    private static final String TAG = "SliderConfigCellBuilder";

    public View build(Context context, Controller controller, Cell cell){

        SliderCell numberCell = cell.sliderCell();

        int[] pallete = ControllerUtil.generateColor(controller, cell);

        LayoutInflater inflater = LayoutInflater.from(context);
        View cellView = inflater.inflate(R.layout.cell_conf_slider, null);
        cellView.setBackgroundColor(pallete[ControllerUtil.INDEX_BUTTON]);

        Drawable icon = Util.getIconDrawable(context, numberCell.getIcon());
        if(icon != null) {
            ImageView imgIcon = (ImageView) cellView.findViewById(R.id.img_icon);
            imgIcon.setImageDrawable(icon);
        }

        return cellView;
    }



    @Override
    public RemoteViews buildRemote(Context context, Controller controller, Cell cell) {
        return null;
    }
}
