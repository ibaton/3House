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

import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.db.controller.CellDB;
import treehou.se.habit.core.db.ServerDB;
import treehou.se.habit.core.db.controller.ControllerDB;
import treehou.se.habit.core.db.controller.SliderCellDB;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.control.CellFactory;
import treehou.se.habit.ui.control.ControllerUtil;
import treehou.se.habit.ui.control.SliderActivity;

public class SliderCellBuilder implements CellFactory.CellBuilder {

    private static final String TAG = "SliderCellBuilder";

    public View build(final Context context, ControllerDB controller, final CellDB cell){

        final SliderCellDB numberCell = cell.sliderCell();

        int[] pallete = ControllerUtil.generateColor(controller, cell);

        LayoutInflater inflater = LayoutInflater.from(context);
        View cellView = inflater.inflate(R.layout.cell_slider, null);
        View viwBackground = cellView.findViewById(R.id.viw_background);
        viwBackground.getBackground().setColorFilter(pallete[ControllerUtil.INDEX_BUTTON], PorterDuff.Mode.MULTIPLY);

        ImageView imgIcon = (ImageView) cellView.findViewById(R.id.img_icon_button);
        imgIcon.setImageDrawable(Util.getIconDrawable(context, numberCell.getIcon()));

        SeekBar sbrNumber = (SeekBar) cellView.findViewById(R.id.sbrNumber);
        sbrNumber.setMax(numberCell.getMax());
        sbrNumber.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(numberCell.getItem() == null){
                    return;
                }

                ServerDB server = numberCell.getItem().getServer();
                Communicator communicator = Communicator.instance(context);
                communicator.command(server, numberCell.getItem(), ""+seekBar.getProgress());
            }
        });

        return cellView;
    }

    @Override
    public RemoteViews buildRemote(final Context context, ControllerDB controller, CellDB cell) {
        final SliderCellDB numberCell = cell.sliderCell();

        RemoteViews cellView = new RemoteViews(context.getPackageName(), R.layout.cell_button);
        cellView.setInt(R.id.cell_button, "setBackgroundColor", cell.getColor());

        Bitmap icon = Util.getIconBitmap(context, numberCell.getIcon());
        if(icon != null) {
            cellView.setImageViewBitmap(R.id.img_icon_button, icon);
        }
        Intent intent = new Intent(context.getApplicationContext(), SliderActivity.class);
        intent.setAction(SliderActivity.ACTION_NUMBER);
        intent.putExtra(SliderActivity.ARG_CELL, cell.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION );

        //TODO give intent unique id
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) (Math.random() * Integer.MAX_VALUE), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        cellView.setOnClickPendingIntent(R.id.img_icon_button, pendingIntent);

        return cellView;
    }
}
