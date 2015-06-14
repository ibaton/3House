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
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.control.CellFactory;
import treehou.se.habit.ui.control.CommandService;
import treehou.se.habit.ui.control.ControllerUtil;

public class ButtonCellBuilder implements CellFactory.CellBuilder {

    private static final String TAG = "ButtonCellBuilder";

    public View build(final Context context, Controller controller, final Cell cell){
        Log.d(TAG, "Build: Button");
        final ButtonCell buttonCell = cell.buttonCell();

        LayoutInflater inflater = LayoutInflater.from(context);
        View cellView = inflater.inflate(R.layout.cell_button, null);

        int[] pallete = ControllerUtil.generateColor(controller, cell);
        cellView.setBackgroundColor(pallete[ControllerUtil.INDEX_BUTTON]);

        ImageButton imgIcon = (ImageButton) cellView.findViewById(R.id.img_icon_button);
        imgIcon.getBackground().setColorFilter(pallete[ControllerUtil.INDEX_BUTTON], PorterDuff.Mode.MULTIPLY);

        Log.d(TAG, "Build: Button icon " + buttonCell.getIcon());

        imgIcon.setImageDrawable(Util.getIconDrawable(context, buttonCell.getIcon()));
        imgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Server server = buttonCell.getItem().getServer();
                Communicator communicator = Communicator.instance(context);
                communicator.command(server, buttonCell.getItem(), buttonCell.getCommand());
            }
        });

        return cellView;
    }

    @Override
    public RemoteViews buildRemote(final Context context, Controller controller, Cell cell) {
        final ButtonCell buttonCell = cell.buttonCell();

        RemoteViews cellView = new RemoteViews(context.getPackageName(), R.layout.cell_button);
        cellView.setInt(R.id.cell_button, "setBackgroundColor", cell.getColor());
        cellView.setImageViewBitmap(R.id.img_icon_button, Util.getIconBitmap(context, buttonCell.getIcon()));
        Intent intent = CommandService.getActionCommand(context, buttonCell.getCommand(), buttonCell.getItem());

        //TODO give intent unique id
        PendingIntent pendingIntent = PendingIntent.getService(context, (int) (Math.random() * Integer.MAX_VALUE), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        cellView.setOnClickPendingIntent(R.id.img_icon_button, pendingIntent);

        return cellView;
    }
}
