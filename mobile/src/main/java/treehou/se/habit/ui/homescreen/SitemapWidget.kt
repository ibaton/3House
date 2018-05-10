package treehou.se.habit.ui.homescreen

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

import treehou.se.habit.R
import treehou.se.habit.ui.main.MainActivity

/**
 * Implementation of App WidgetFactory functionality.
 * App WidgetFactory Configuration implemented in [SitemapWidgetConfigureActivity]
 */
class SitemapWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            SitemapWidgetConfigureActivity.deletePref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {

            val sitemap = SitemapWidgetConfigureActivity.loadSitemap(context, appWidgetId)

            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.sitemap_widget)

            if (sitemap != null) {
                views.setTextViewText(R.id.lbl_sitemap_name, sitemap.name)

                val intent = Intent(context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                //intent.putExtra(MainActivity.EXTRA_SHOW_SITEMAP, sitemap.getId());
                val pendingIntent = PendingIntent.getActivity(context, (Math.random() * 10000).toInt(), intent, 0)

                views.setOnClickPendingIntent(R.id.btn_open_sitemap, pendingIntent)
            } else {
                views.setTextViewText(R.id.lbl_sitemap_name, "Failed")
            }

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

