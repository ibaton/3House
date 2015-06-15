package treehou.se.habit.ui.control.builders;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RemoteViews;

import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.db.controller.CellDB;
import treehou.se.habit.core.db.controller.ControllerDB;
import treehou.se.habit.core.db.controller.IncDecCellDB;
import treehou.se.habit.core.db.ServerDB;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.control.CellFactory;
import treehou.se.habit.ui.control.CommandService;
import treehou.se.habit.ui.control.ControllerUtil;

/**
 * Created by ibaton on 2014-11-08.
 */
public class IncDecCellBuilder implements CellFactory.CellBuilder {

    private static final String TAG = "IncDecCellBuilder";

    public View build(final Context context, ControllerDB controller, final CellDB cell){
        Log.d(TAG, "Build: Button");

        final IncDecCellDB buttonCell = cell.incDecCell();

        LayoutInflater inflater = LayoutInflater.from(context);
        View cellView = inflater.inflate(R.layout.cell_button, null);

        int[] pallete = ControllerUtil.generateColor(controller, cell);
        cellView.setBackgroundColor(pallete[ControllerUtil.INDEX_BUTTON]);

        ImageButton imgIcon = (ImageButton) cellView.findViewById(R.id.img_icon_button);
        imgIcon.getBackground().setColorFilter(pallete[ControllerUtil.INDEX_BUTTON], PorterDuff.Mode.MULTIPLY);

        Log.d(TAG, "Build: Button icon " + buttonCell.getIcon());

        Drawable icon = Util.getIconDrawable(context, buttonCell.getIcon());
        if(icon != null) {
            imgIcon.setImageDrawable(icon);
            imgIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ServerDB server = buttonCell.getItem().getServer();
                    Communicator communicator = Communicator.instance(context);
                    communicator.incDec(server, buttonCell.getItem(), buttonCell.getValue(), buttonCell.getMin(), buttonCell.getMax());
                }
            });
        }

        return cellView;
    }

    @Override
    public RemoteViews buildRemote(final Context context, ControllerDB controller, CellDB cell) {
        final IncDecCellDB buttonCell = cell.incDecCell();

        RemoteViews cellView = new RemoteViews(context.getPackageName(), R.layout.cell_button);
        cellView.setInt(R.id.cell_button, "setBackgroundColor", cell.getColor());
        Bitmap icon = Util.getIconBitmap(context, buttonCell.getIcon());
        if(icon != null) {
            cellView.setImageViewBitmap(R.id.img_icon_button, icon);
        }
        Intent intent = CommandService.getActionIncDec(context, buttonCell.getMin(), buttonCell.getMax(), buttonCell.getValue(), buttonCell.getItem());

        //TODO give intent unique id
        PendingIntent pendingIntent = PendingIntent.getService(context, (int) (Math.random() * Integer.MAX_VALUE), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        cellView.setOnClickPendingIntent(R.id.img_icon_button, pendingIntent);

        return cellView;
    }
}
