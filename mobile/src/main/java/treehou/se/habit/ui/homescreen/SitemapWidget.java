package treehou.se.habit.ui.homescreen;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import treehou.se.habit.MainActivity;
import treehou.se.habit.R;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link SitemapWidgetConfigureActivity SitemapWidgetConfigureActivity}
 */
public class SitemapWidget extends AppWidgetProvider {

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
            SitemapWidgetConfigureActivity.deletePref(context, appWidgetId);
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

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        OHSitemap sitemap = SitemapWidgetConfigureActivity.loadSitemap(context, appWidgetId);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.sitemap_widget);

        if(sitemap != null) {
            views.setTextViewText(R.id.lbl_sitemap_name, sitemap.getName());

            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //intent.putExtra(MainActivity.EXTRA_SHOW_SITEMAP, sitemap.getId());
            PendingIntent pendingIntent = PendingIntent.getActivity(context, (int)(Math.random() * 10000), intent, 0);

            views.setOnClickPendingIntent(R.id.btn_open_sitemap, pendingIntent);
        }
        else {
            views.setTextViewText(R.id.lbl_sitemap_name, "Failed");
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

