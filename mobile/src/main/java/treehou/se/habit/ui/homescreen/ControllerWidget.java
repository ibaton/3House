package treehou.se.habit.ui.homescreen;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.RemoteViews;

import treehou.se.habit.R;
import treehou.se.habit.core.controller.Cell;
import treehou.se.habit.core.controller.CellRow;
import treehou.se.habit.core.controller.Controller;
import treehou.se.habit.ui.control.builders.ButtonCellBuilder;
import treehou.se.habit.ui.control.CellFactory;
import treehou.se.habit.ui.control.builders.EmptyCellBuilder;
import treehou.se.habit.ui.control.builders.IncDecCellBuilder;
import treehou.se.habit.ui.control.builders.SliderCellBuilder;
import treehou.se.habit.ui.control.builders.VoiceCellBuilder;

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
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        long controllId = ControllerWidgetConfigureActivity.loadControllIdPref(context, appWidgetId);
        boolean showTitle = ControllerWidgetConfigureActivity.loadControllShowTitlePref(context, appWidgetId);

        Controller controller = Controller.load(Controller.class, controllId);
        if(controller == null){
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.error_widget);
            appWidgetManager.updateAppWidget(appWidgetId, views);
            return;
        }

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.controller_widget);

        views.setInt(R.id.lou_widget, "setBackgroundColor", controller.getColor());
        views.setInt(R.id.lou_rows, "setBackgroundColor", controller.getColor());

        views.setTextViewText(R.id.lbl_title, controller.getName());
        views.setViewVisibility(R.id.lbl_title, showTitle?View.VISIBLE:View.GONE);

        redrawController(context, views, controller);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void redrawController(Context context, RemoteViews rows, Controller controller){

        rows.removeAllViews(R.id.lou_rows);

        CellFactory<Integer> cellFactory = new CellFactory<>();
        cellFactory.setDefaultBuilder(new EmptyCellBuilder());
        cellFactory.addBuilder(Cell.TYPE_BUTTON, new ButtonCellBuilder());
        cellFactory.addBuilder(Cell.TYPE_SLIDER, new SliderCellBuilder());
        cellFactory.addBuilder(Cell.TYPE_INC_DEC, new IncDecCellBuilder());
        cellFactory.addBuilder(Cell.TYPE_VOICE, new VoiceCellBuilder());

        for (final CellRow row : controller.cellRows()) {
            RemoteViews rowView = new RemoteViews(context.getPackageName(), R.layout.homescreen_widget_row);

            for (final Cell cell : row.cells()) {
                RemoteViews itemView = cellFactory.createRemote(context, controller, cell);
                rowView.addView(R.id.lou_row, itemView);
            }
            rows.addView(R.id.lou_rows, rowView);
        }
    }
}


