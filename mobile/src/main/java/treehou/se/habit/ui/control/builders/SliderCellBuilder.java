package treehou.se.habit.ui.control.builders;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.controller.CellDB;
import treehou.se.habit.core.db.model.controller.ControllerDB;
import treehou.se.habit.core.db.model.controller.SliderCellDB;
import treehou.se.habit.ui.util.ViewHelper;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.control.CellFactory;
import treehou.se.habit.ui.control.ControllerUtil;
import treehou.se.habit.ui.control.SliderActivity;

public class SliderCellBuilder implements CellFactory.CellBuilder {

    private static final String TAG = "SliderCellBuilder";

    @BindView(R.id.img_icon_button) ImageView imgIcon;
    @BindView(R.id.sbrNumber) SeekBar sbrNumber;
    @BindView(R.id.viw_background) View viwBackground;

    public View build(final Context context, ControllerDB controller, final CellDB cell){
        LayoutInflater inflater = LayoutInflater.from(context);
        View cellView = inflater.inflate(R.layout.cell_slider, null);
        ButterKnife.bind(this, cellView);

        Realm realm = Realm.getDefaultInstance();
        final SliderCellDB sliderCell = SliderCellDB.getCell(realm, cell);

        int[] pallete = ControllerUtil.generateColor(controller, cell);

        viwBackground.getBackground().setColorFilter(pallete[ControllerUtil.INDEX_BUTTON], PorterDuff.Mode.MULTIPLY);

        imgIcon.setImageDrawable(Util.getIconDrawable(context, sliderCell.getIcon()));
        sbrNumber.setMax(sliderCell.getMax());
        sbrNumber.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(sliderCell.getItem() == null){
                    return;
                }

                OHServer server = sliderCell.getItem().getServer().toGeneric();
                IServerHandler serverHandler = new Connector.ServerHandler(server, context);
                serverHandler.sendCommand(sliderCell.getItem().getName(), ""+seekBar.getProgress());
            }
        });
        realm.close();

        return cellView;
    }

    @Override
    public RemoteViews buildRemote(final Context context, ControllerDB controller, CellDB cell) {
        Realm realm = Realm.getDefaultInstance();
        final SliderCellDB numberCell = SliderCellDB.getCell(realm, cell);

        RemoteViews cellView = new RemoteViews(context.getPackageName(), R.layout.cell_button);

        int[] pallete = ControllerUtil.generateColor(controller, cell);
        ViewHelper.colorRemoteDrawable(cellView, R.id.img_icon_button, pallete[ControllerUtil.INDEX_BUTTON]);

        Bitmap icon = Util.getIconBitmap(context, numberCell.getIcon());
        if(icon != null) {
            cellView.setImageViewBitmap(R.id.img_icon_button, icon);
        }

        //TODO give intent unique id
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) (Math.random() * Integer.MAX_VALUE), createSliderIntent(context, cell.getId()), PendingIntent.FLAG_UPDATE_CURRENT);
        cellView.setOnClickPendingIntent(R.id.img_icon_button, pendingIntent);
        realm.close();

        return cellView;
    }

    /**
     * Create a intent that can launch a slider activity.
     *
     * @param context get context launching activity
     * @param cellID the cell id
     * @return intent that can launch activity.
     */
    private Intent createSliderIntent(Context context, long cellID){
        Intent intent = new Intent(context.getApplicationContext(), SliderActivity.class);
        intent.setAction(SliderActivity.ACTION_NUMBER);
        intent.putExtra(SliderActivity.ARG_CELL, cellID);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION );
        return intent;
    }
}
