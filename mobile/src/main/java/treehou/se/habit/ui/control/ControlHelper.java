package treehou.se.habit.ui.control;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import treehou.se.habit.R;
import treehou.se.habit.core.controller.Cell;
import treehou.se.habit.core.controller.CellRow;
import treehou.se.habit.core.controller.Controller;
import treehou.se.habit.ui.control.builders.ButtonCellBuilder;
import treehou.se.habit.ui.control.builders.EmptyCellBuilder;
import treehou.se.habit.ui.control.builders.IncDecCellBuilder;
import treehou.se.habit.ui.control.builders.SliderCellBuilder;
import treehou.se.habit.ui.control.builders.VoiceCellBuilder;

public class ControlHelper {

    private static final String TAG = "ControlHelper";

    private ControlHelper(){}

    /**
     * Populate remote view with controller cells
     *
     * @param rows
     * @return
     */
    public static RemoteViews drawRemoteController(Context context, RemoteViews rows, Controller controller){

        Log.d(TAG, "Drawing remote controller");

        CellFactory<Integer> cellFactory = new CellFactory<>();
        cellFactory.setDefaultBuilder(new EmptyCellBuilder());
        cellFactory.addBuilder(Cell.TYPE_BUTTON, new ButtonCellBuilder());
        cellFactory.addBuilder(Cell.TYPE_SLIDER, new SliderCellBuilder());
        cellFactory.addBuilder(Cell.TYPE_INC_DEC, new IncDecCellBuilder());
        cellFactory.addBuilder(Cell.TYPE_VOICE, new VoiceCellBuilder());

        for (final CellRow row : controller.cellRows()) {
            Log.d(TAG, "Rows " + controller.cellRows().size());
            RemoteViews rowView = new RemoteViews(context.getPackageName(), R.layout.homescreen_widget_row);

            for (final Cell cell : row.cells()) {
                RemoteViews itemView = cellFactory.createRemote(context, controller, cell);
                rowView.addView(R.id.lou_row, itemView);
            }
            rows.addView(R.id.lou_rows, rowView);
        }
        return rows;
    }

    /**
     * Show remote view as notification
     */
    public static void showNotification(Context context, Controller controller) {

        Log.d(TAG, "Show controller as notification");

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.controller_widget);
        views.removeAllViews(R.id.lou_rows);
        views.setInt(R.id.lou_widget, "setBackgroundColor", controller.getColor());
        views.setInt(R.id.lou_rows, "setBackgroundColor", controller.getColor());
        views.setViewVisibility(R.id.lbl_title, View.GONE);

        ControlHelper.drawRemoteController(context, views, controller);

        android.app.Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setOngoing(true)
                .setContent(views)
                .build();

        NotificationManagerCompat.from(context).notify(controller.getId().intValue(), notification);
    }

    /**
     * Hide controller notification
     *
     * @param context
     * @param controller
     */
    public static void hideNotification(Context context, Controller controller) {

        Log.d(TAG, "Hide controller notification");

        NotificationManagerCompat.from(context).cancel(controller.getId().intValue());
    }
}
