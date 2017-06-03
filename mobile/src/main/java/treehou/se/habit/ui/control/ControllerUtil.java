package treehou.se.habit.ui.control;

import android.app.Notification;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.mattyork.colours.Colour;

import io.realm.Realm;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.controller.CellDB;
import treehou.se.habit.core.db.model.controller.CellRowDB;
import treehou.se.habit.core.db.model.controller.ControllerDB;
import treehou.se.habit.ui.homescreen.ControllerWidget;
import treehou.se.habit.util.Util;

public class ControllerUtil {

    private static final String TAG = ControllerUtil.class.getSimpleName();

    public static final int INDEX_BUTTON = 0;

    private Context context;
    private CellFactory<Integer> cellFactory;
    private Realm realm;

    public ControllerUtil(Context context, Realm realm, CellFactory<Integer> cellFactory) {
        this.context = context;
        this.cellFactory = cellFactory;
        this.realm = realm;
    }

    public static int[] generateColor(ControllerDB controller, CellDB cell) {
        return generateColor(controller, cell, true);
    }

    public static int[] generateColor(ControllerDB controller, CellDB cell, boolean preventInvis) {
        int[] pallete;
        if (Colour.alpha(cell.getColor()) < 150) {
            if (preventInvis && Colour.alpha(controller.getColor()) < 150) {
                pallete = Util.generatePallete(Color.LTGRAY);
            }else {
                pallete = Util.generatePallete(controller.getColor());
            }
        } else {
            pallete = Util.generatePallete(cell.getColor());
        }

        return pallete;
    }

    public void updateWidget(int widgetId){
        Intent intent = new Intent(context, ControllerWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = {widgetId};
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        context.sendBroadcast(intent);
    }

    /**
     * Populate remote view with controller cells
     *
     * @param rows
     * @return
     */
    public RemoteViews drawRemoteController(RemoteViews rows, ControllerDB controller){

        Log.d(TAG, "Drawing remote controller");
        for (final CellRowDB row : controller.getCellRows()) {
            Log.d(TAG, "Rows " + controller.getCellRows().size());
            RemoteViews rowView = new RemoteViews(context.getPackageName(), R.layout.homescreen_widget_row);

            for (final CellDB cell : row.getCells()) {
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
    public void showNotification(ControllerDB controller) {

        Log.d(TAG, "Show controller as notification");

        if(controller.isShowNotification() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.controller_widget);
            views.removeAllViews(R.id.lou_rows);
            views.setInt(R.id.lou_widget, "setBackgroundColor", controller.getColor());
            views.setInt(R.id.lou_rows, "setBackgroundColor", controller.getColor());
            views.setViewVisibility(R.id.lbl_title, View.GONE);

            drawRemoteController(views, controller);

            Notification notification = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContent(views)
                    .build();

            NotificationManagerCompat.from(context).notify((int) controller.getId(), notification);
        }
    }

    /**
     * Show all controllers as notifications.
     *
     * @param context
     */
    public void showNotifications(Context context) {
        NotificationManagerCompat.from(context).cancelAll();
        for(ControllerDB controller : realm.where(ControllerDB.class).findAll()) {
            if (controller.isShowNotification()) {
                showNotification(controller);
            }
        }
        realm.close();
    }

    /**
     * Hide controller notification
     *
     * @param controller
     */
    public void hideNotification(ControllerDB controller) {
        NotificationManagerCompat.from(context).cancel((int) controller.getId());
    }
}
