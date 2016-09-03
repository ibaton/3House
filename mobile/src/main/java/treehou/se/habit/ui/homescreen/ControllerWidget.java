package treehou.se.habit.ui.homescreen;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;

import io.realm.Realm;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.controller.ControllerDB;
import treehou.se.habit.ui.control.ControllerUtil;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link ControllerWidgetConfigureActivity ControllerWidgetConfigureActivity}
 */
public class ControllerWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            ControllerWidgetConfigureActivity.deletePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDisabled(Context context) {
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Realm realm = Realm.getDefaultInstance();

        long controllId = ControllerWidgetConfigureActivity.loadControllIdPref(context, appWidgetId);
        boolean showTitle = ControllerWidgetConfigureActivity.loadControllShowTitlePref(context, appWidgetId);

        ControllerDB controller = ControllerDB.load(realm, controllId);
        if(controller == null){
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.error_widget);
            appWidgetManager.updateAppWidget(appWidgetId, views);
            return;
        }

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.controller_widget);
        views.removeAllViews(R.id.lou_rows);

        views.setInt(R.id.lou_widget, "setBackgroundColor", controller.getColor());
        views.setInt(R.id.lou_rows, "setBackgroundColor", controller.getColor());

        views.setTextViewText(R.id.lbl_title, controller.getName());
        views.setViewVisibility(R.id.lbl_title, showTitle ? View.VISIBLE:View.GONE);

        ControllerUtil.drawRemoteController(context, views, controller);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

        realm.close();
    }
}


