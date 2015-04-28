package treehou.se.habit.ui.control.builders;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RemoteViews;

import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.Server;
import treehou.se.habit.core.controller.ButtonCell;
import treehou.se.habit.core.controller.Cell;
import treehou.se.habit.core.controller.Controller;
import treehou.se.habit.core.controller.IncDecCell;
import treehou.se.habit.ui.Util;
import treehou.se.habit.ui.control.CellFactory;
import treehou.se.habit.ui.control.CommandService;
import treehou.se.habit.ui.control.ControllerUtil;
import treehou.se.habit.ui.control.Icon;

/**
 * Created by ibaton on 2014-11-08.
 */
public class IncDecCellBuilder implements CellFactory.CellBuilder {

    private static final String TAG = "SwitchConfigCellBuilder";

    public View build(final Context context, Controller controller, final Cell cell){
        Log.d(TAG, "Build: Button");

        final IncDecCell buttonCell = cell.incDecCell();

        LayoutInflater inflater = LayoutInflater.from(context);
        View cellView = inflater.inflate(R.layout.cell_button, null);

        int[] pallete = ControllerUtil.generateColor(controller, cell);
        cellView.setBackgroundColor(pallete[ControllerUtil.INDEX_BUTTON]);

        Icon icon = Util.getIcon(context, buttonCell.getIcon());

        ImageButton imgIcon = (ImageButton) cellView.findViewById(R.id.img_icon_button);
        imgIcon.getBackground().setColorFilter(pallete[ControllerUtil.INDEX_BUTTON], PorterDuff.Mode.MULTIPLY);

        Log.d(TAG, "Build: Button icon " + buttonCell.getIcon());

        if(icon != null) {
            imgIcon.setImageResource(icon.getResource());
            imgIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Server server = buttonCell.getItem().getServer();
                    Communicator communicator = Communicator.instance(context);
                    communicator.incDec(server, buttonCell.getItem(), buttonCell.getValue(), buttonCell.getMin(), buttonCell.getMax());
                }
            });
        }

        return cellView;
    }

    @Override
    public RemoteViews buildRemote(final Context context, Controller controller, Cell cell) {
        final IncDecCell buttonCell = cell.incDecCell();

        RemoteViews cellView = new RemoteViews(context.getPackageName(), R.layout.cell_button);
        cellView.setInt(R.id.cell_button, "setBackgroundColor", cell.getColor());
        Icon icon = Util.getIcon(context, buttonCell.getIcon());
        if(icon != null) {
            cellView.setImageViewResource(R.id.img_icon_button, icon.getResource());
        }
        Intent intent = CommandService.getActionIncDec(context, buttonCell.getMin(), buttonCell.getMax(), buttonCell.getValue(), buttonCell.getItem());

        //TODO give intent unique id
        PendingIntent pendingIntent = PendingIntent.getService(context, (int) (Math.random() * Integer.MAX_VALUE), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        cellView.setOnClickPendingIntent(R.id.img_icon_button, pendingIntent);

        return cellView;
    }
}
