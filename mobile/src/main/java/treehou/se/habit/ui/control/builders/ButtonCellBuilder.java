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

import io.realm.Realm;
import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.ItemDB;
import treehou.se.habit.core.db.model.controller.ButtonCellDB;
import treehou.se.habit.core.db.model.controller.CellDB;
import treehou.se.habit.core.db.model.controller.ControllerDB;
import treehou.se.habit.ui.control.CommandService;
import treehou.se.habit.ui.util.ViewHelper;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.control.CellFactory;
import treehou.se.habit.ui.control.ControllerUtil;

public class ButtonCellBuilder implements CellFactory.CellBuilder {

    private static final String TAG = "ButtonCellBuilder";

    public View build(final Context context, ControllerDB controller, final CellDB cell){
        Log.d(TAG, "Build: Button");
        Realm realm = Realm.getDefaultInstance();
        final ButtonCellDB buttonCell = ButtonCellDB.getCell(realm, cell);

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
                ItemDB item = buttonCell.getItem();
                if(item != null) {
                    OHServer server = item.getServer().toGeneric();
                    Openhab.instance(server).sendCommand(item.getName(), buttonCell.getCommand());
                }
            }
        });
        realm.close();

        return cellView;
    }



    @Override
    public RemoteViews buildRemote(final Context context, ControllerDB controller, CellDB cell) {

        Realm realm = Realm.getDefaultInstance();
        final ButtonCellDB buttonCell = ButtonCellDB.getCell(realm, cell);

        RemoteViews cellView = new RemoteViews(context.getPackageName(), R.layout.cell_button);

        int[] pallete = ControllerUtil.generateColor(controller, cell);
        ViewHelper.colorRemoteDrawable(cellView, R.id.img_icon_button, pallete[ControllerUtil.INDEX_BUTTON]);

        cellView.setImageViewBitmap(R.id.img_icon_button, Util.getIconBitmap(context, buttonCell.getIcon()));
        Intent intent = CommandService.getActionCommand(context, buttonCell.getCommand(), buttonCell.getItem().getId());

        //TODO give intent unique id
        PendingIntent pendingIntent = PendingIntent.getService(context, (int) (Math.random() * Integer.MAX_VALUE), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        cellView.setOnClickPendingIntent(R.id.img_icon_button, pendingIntent);
        realm.close();

        return cellView;
    }
}
