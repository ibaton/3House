package treehou.se.habit.ui.control.builders;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SeekBar;

import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.Server;
import treehou.se.habit.core.controller.Cell;
import treehou.se.habit.core.controller.Controller;
import treehou.se.habit.core.controller.SliderCell;
import treehou.se.habit.ui.Util;
import treehou.se.habit.ui.control.CellFactory;
import treehou.se.habit.ui.control.ControllerUtil;
import treehou.se.habit.ui.control.Icon;
import treehou.se.habit.ui.control.SliderActivity;

/**
 * Created by ibaton on 2014-11-08.
 */
public class SliderCellBuilder implements CellFactory.CellBuilder {

    private static final String TAG = "SliderCellBuilder";

    public View build(final Context context, Controller controller, final Cell cell){

        final SliderCell numberCell = cell.sliderCell();

        int[] pallete = ControllerUtil.generateColor(controller, cell);

        LayoutInflater inflater = LayoutInflater.from(context);
        View cellView = inflater.inflate(R.layout.cell_slider, null);
        cellView.setBackgroundColor(pallete[ControllerUtil.INDEX_BUTTON]);

        Icon icon = Util.getIcon(context, numberCell.getIcon());

        if(icon != null) {
            ImageView imgIcon = (ImageView) cellView.findViewById(R.id.img_icon_button);
            imgIcon.setImageResource(icon.getResource());
        }

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
                Server server = numberCell.getItem().getServer();
                Communicator communicator = Communicator.instance(context);
                communicator.command(server, numberCell.getItem(), ""+seekBar.getProgress());
            }
        });

        return cellView;
    }

    @Override
    public RemoteViews buildRemote(final Context context, Controller controller, Cell cell) {
        final SliderCell numberCell = cell.sliderCell();

        RemoteViews cellView = new RemoteViews(context.getPackageName(), R.layout.cell_button);
        cellView.setInt(R.id.cell_button, "setBackgroundColor", cell.getColor());

        Icon icon = Util.getIcon(context, numberCell.getIcon());
        if(icon != null) {
            cellView.setImageViewResource(R.id.img_icon_button, icon.getResource());
        }
        Intent intent = new Intent(context.getApplicationContext(), SliderActivity.class);
        intent.setAction(SliderActivity.ACTION_NUMBER);
        intent.putExtra(SliderActivity.ARG_CELL, cell.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        //TODO give intent unique id
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) (Math.random() * Integer.MAX_VALUE), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        cellView.setOnClickPendingIntent(R.id.img_icon_button, pendingIntent);

        return cellView;
    }
}
