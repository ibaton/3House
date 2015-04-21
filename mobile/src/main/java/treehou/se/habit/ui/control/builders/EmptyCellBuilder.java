package treehou.se.habit.ui.control.builders;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RemoteViews;

import treehou.se.habit.R;
import treehou.se.habit.core.controller.ButtonCell;
import treehou.se.habit.core.controller.Cell;
import treehou.se.habit.core.controller.Controller;
import treehou.se.habit.ui.control.CellFactory;
import treehou.se.habit.ui.control.ControllerUtil;

/**
 * Created by ibaton on 2014-11-08.
 */
public class EmptyCellBuilder implements CellFactory.CellBuilder {

    public View build(Context context, Controller controller, Cell cell){

        int[] pallete = ControllerUtil.generateColor(controller, cell);

        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.cell_empty, null);
        rootView.setBackgroundColor(pallete[ControllerUtil.INDEX_BUTTON]);
        ImageButton imgButton = (ImageButton) rootView.findViewById(R.id.img_icon_button);
        imgButton.getBackground().setColorFilter(pallete[ControllerUtil.INDEX_BUTTON], PorterDuff.Mode.MULTIPLY);

        return rootView;
    }

    @Override
    public RemoteViews buildRemote(Context context, Controller controller, Cell cell) {
        final ButtonCell buttonCell = cell.buttonCell();

        RemoteViews cellView = new RemoteViews(context.getPackageName(), R.layout.cell_empty);
        cellView.setInt(R.id.lou_base_empty, "setBackgroundColor", cell.getColor());

        return cellView;
    }
}
