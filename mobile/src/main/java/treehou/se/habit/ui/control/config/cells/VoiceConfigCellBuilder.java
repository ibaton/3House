package treehou.se.habit.ui.control.config.cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RemoteViews;

import treehou.se.habit.R;
import treehou.se.habit.core.controller.Cell;
import treehou.se.habit.core.controller.Controller;
import treehou.se.habit.core.controller.VoiceCell;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.control.CellFactory;
import treehou.se.habit.ui.control.ControllerUtil;

public class VoiceConfigCellBuilder implements CellFactory.CellBuilder {

    private static final String TAG = "VoiceConfigCellBuilder";

    public View build(Context context, Controller controller, Cell cell){

        VoiceCell voiceCell = cell.voiceCell();

        int[] pallete = ControllerUtil.generateColor(controller, cell);

        LayoutInflater inflater = LayoutInflater.from(context);
        View cellView = inflater.inflate(R.layout.cell_conf_button, null);
        cellView.setBackgroundColor(pallete[ControllerUtil.INDEX_BUTTON]);

        Drawable icon = Util.getIconDrawable(context, voiceCell.getIcon());
        ImageButton imgIcon = (ImageButton) cellView.findViewById(R.id.img_icon_button);
        imgIcon.getBackground().setColorFilter(pallete[ControllerUtil.INDEX_BUTTON], PorterDuff.Mode.MULTIPLY);
        if(icon != null){
            imgIcon.setImageDrawable(icon);
        }

        return cellView;
    }

    @Override
    public RemoteViews buildRemote(Context context, Controller controller, Cell cell) {
        return null;
    }
}
