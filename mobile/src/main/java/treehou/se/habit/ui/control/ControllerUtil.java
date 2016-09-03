package treehou.se.habit.ui.control;

import android.app.Notification;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.mattyork.colours.Colour;

import java.util.List;

import io.realm.Realm;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.controller.CellDB;
import treehou.se.habit.core.db.model.controller.CellRowDB;
import treehou.se.habit.core.db.model.controller.ControllerDB;
import treehou.se.habit.ui.control.builders.ButtonCellBuilder;
import treehou.se.habit.ui.control.builders.EmptyCellBuilder;
import treehou.se.habit.ui.control.builders.IncDecCellBuilder;
import treehou.se.habit.ui.control.builders.SliderCellBuilder;
import treehou.se.habit.ui.control.builders.VoiceCellBuilder;
import treehou.se.habit.util.Util;

public class ControllerUtil {

    private static final String TAG = ControllerUtil.class.getSimpleName();

    public static final int INDEX_BUTTON = 0;

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
    public static void showNotification(Context context, ControllerDB controller) {

        Log.d(TAG, "Show controller as notification");

        if(controller.isShowNotification() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.controller_widget);
            views.removeAllViews(R.id.lou_rows);
            views.setInt(R.id.lou_widget, "setBackgroundColor", controller.getColor());
            views.setInt(R.id.lou_rows, "setBackgroundColor", controller.getColor());
            views.setViewVisibility(R.id.lbl_title, View.GONE);

            ControllerUtil.drawRemoteController(context, views, controller);

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
    public static void showNotifications(Context context) {
        Realm realm = Realm.getDefaultInstance();
        for(ControllerDB controller : realm.where(ControllerDB.class).findAll()) {
            if (controller.isShowNotification()) {
                ControllerUtil.showNotification(context, controller);
            } else {
                ControllerUtil.hideNotification(context, controller);
            }
        }
        realm.close();
    }

    /**
     * Hide controller notification
     *
     * @param context
     * @param controller
     */
    public static void hideNotification(Context context, ControllerDB controller) {
        NotificationManagerCompat.from(context).cancel((int) controller.getId());
    }
}
