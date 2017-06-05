package treehou.se.habit.ui.control.cells.builders;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.core.db.model.controller.CellDB;
import treehou.se.habit.core.db.model.controller.ControllerDB;
import treehou.se.habit.core.db.model.controller.IncDecCellDB;
import treehou.se.habit.ui.util.ViewHelper;
import treehou.se.habit.util.ConnectionFactory;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.control.CellFactory;
import treehou.se.habit.ui.control.CommandService;
import treehou.se.habit.ui.control.ControllerUtil;

public class IncDecCellBuilder implements CellFactory.CellBuilder {

    private static final String TAG = "IncDecCellBuilder";

    @BindView(R.id.img_icon_button) ImageButton imgIcon;

    private Communicator communicator;

    public IncDecCellBuilder(Communicator communicator) {
        this.communicator = communicator;
    }

    public View build(final Context context, ControllerDB controller, final CellDB cell){
        LayoutInflater inflater = LayoutInflater.from(context);
        View cellView = inflater.inflate(R.layout.cell_button, null);
        ButterKnife.bind(this, cellView);

        Realm realm = Realm.getDefaultInstance();
        final IncDecCellDB buttonCell = cell.getCellIncDec();

        int[] pallete = ControllerUtil.generateColor(controller, cell);
        imgIcon.getBackground().setColorFilter(pallete[ControllerUtil.INDEX_BUTTON], PorterDuff.Mode.MULTIPLY);

        Log.d(TAG, "Build: Button icon " + buttonCell.getIcon());

        Drawable icon = Util.getIconDrawable(context, buttonCell.getIcon());
        if(icon != null) {
            imgIcon.setImageDrawable(icon);
            imgIcon.setOnClickListener(v -> {
                ServerDB server = buttonCell.getItem().getServer();
                communicator.incDec(server.toGeneric(), buttonCell.getItem().getName(), buttonCell.getValue(), buttonCell.getMin(), buttonCell.getMax());
            });
        }
        realm.close();

        return cellView;
    }

    @Override
    public RemoteViews buildRemote(final Context context, ControllerDB controller, CellDB cell) {
        Realm realm = Realm.getDefaultInstance();
        final IncDecCellDB buttonCell = cell.getCellIncDec();

        RemoteViews cellView = new RemoteViews(context.getPackageName(), R.layout.cell_button);

        int[] pallete = ControllerUtil.generateColor(controller, cell);
        ViewHelper.colorRemoteDrawable(cellView, R.id.img_icon_button, pallete[ControllerUtil.INDEX_BUTTON]);
        Bitmap icon = Util.getIconBitmap(context, buttonCell.getIcon());
        if(icon != null) {
            cellView.setImageViewBitmap(R.id.img_icon_button, icon);
        }
        Intent intent = CommandService.getActionIncDec(context, buttonCell.getMin(), buttonCell.getMax(), buttonCell.getValue(), buttonCell.getItem().getId());
        PendingIntent pendingIntent = PendingIntent.getService(context, (int) (Math.random() * Integer.MAX_VALUE), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        cellView.setOnClickPendingIntent(R.id.img_icon_button, pendingIntent);
        realm.close();

        return cellView;
    }
}
