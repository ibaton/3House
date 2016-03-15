package treehou.se.habit.ui.control;

import android.app.Notification;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import se.treehou.ng.ohcommunicator.core.db.OHRealm;
import treehou.se.habit.R;
import treehou.se.habit.core.db.OHTreehouseRealm;
import treehou.se.habit.core.db.controller.CellDB;
import treehou.se.habit.core.db.controller.CellRowDB;
import treehou.se.habit.core.db.controller.ControllerDB;
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
    public static RemoteViews drawRemoteController(Context context, RemoteViews rows, ControllerDB controller){

        Log.d(TAG, "Drawing remote controller");

        CellFactory<Integer> cellFactory = new CellFactory<>();
        cellFactory.setDefaultBuilder(new EmptyCellBuilder());
        cellFactory.addBuilder(CellDB.TYPE_BUTTON, new ButtonCellBuilder());
        cellFactory.addBuilder(CellDB.TYPE_SLIDER, new SliderCellBuilder());
        cellFactory.addBuilder(CellDB.TYPE_INC_DEC, new IncDecCellBuilder());
        cellFactory.addBuilder(CellDB.TYPE_VOICE, new VoiceCellBuilder());

        /*for (final CellRowDB row : controller.getCellRows()) {
            Log.d(TAG, "Rows " + controller.getCellRows().size());
            RemoteViews rowView = new RemoteViews(context.getPackageName(), R.layout.homescreen_widget_row);

            for (final CellDB cell : row.getCells()) {
                RemoteViews itemView = cellFactory.createRemote(context, controller, cell);
                rowView.addView(R.id.lou_row, itemView);
            }
            rows.addView(R.id.lou_rows, rowView);
        }*/
        return rows;
    }

    /**
     * Show remote view as notification
     */
    public static void showNotification(Context context, ControllerDB controller) {

        Log.d(TAG, "Show controller as notification");

        if(controller.isShowNotification() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.controller_widget);
            views.removeAllViews(R.id.lou_rows);
            views.setInt(R.id.lou_widget, "setBackgroundColor", controller.getColor());
            views.setInt(R.id.lou_rows, "setBackgroundColor", controller.getColor());
            views.setViewVisibility(R.id.lbl_title, View.GONE);

            ControlHelper.drawRemoteController(context, views, controller);

            Notification notification = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setOngoing(true)
                    .setContent(views)
                    .build();

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                notification.bigContentView = views;
            }*/

            NotificationManagerCompat.from(context).notify((int) controller.getId(), notification);
        }
    }

    /**
     * Hide controller notification
     *
     * @param context
     * @param controller
     */
    public static void hideNotification(Context context, ControllerDB controller) {

        Log.d(TAG, "Hide controller notification");

        NotificationManagerCompat.from(context).cancel((int) controller.getId());
    }

    /**
     * Show all controllers as notifications.
     *
     * @param context
     */
    public static void showNotifications(Context context) {
        /*NotificationManagerCompat.from(context).cancelAll();
        for(ControllerDB controller : OHTreehouseRealm.realm().where(ControllerDB.class).findAll()){
            if(controller.isShowNotification()){
                showNotification(context, controller);
            }
        }*/
    }
}
