package treehou.se.habit.ui.homescreen

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews

import javax.inject.Inject

import io.realm.Realm
import treehou.se.habit.HabitApplication
import treehou.se.habit.R
import treehou.se.habit.core.db.model.controller.ControllerDB
import treehou.se.habit.ui.control.ControllerUtil

/**
 * Implementation of App WidgetFactory functionality.
 * App WidgetFactory Configuration implemented in [ControllerWidgetConfigureActivity]
 */
class ControllerWidget : AppWidgetProvider() {

    @Inject lateinit var controllerUtil: ControllerUtil

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            ControllerWidgetConfigureActivity.deletePref(context, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        (context.applicationContext as HabitApplication).component().inject(this)
        super.onReceive(context, intent)
    }

    override fun onEnabled(context: Context) {}

    override fun onDisabled(context: Context) {}

    internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                 appWidgetId: Int) {

        val realm = Realm.getDefaultInstance()

        val controllId = ControllerWidgetConfigureActivity.loadControllIdPref(context, appWidgetId)
        val showTitle = ControllerWidgetConfigureActivity.loadControllShowTitlePref(context, appWidgetId)

        val controller = ControllerDB.load(realm, controllId)
        if (controller == null) {
            val views = RemoteViews(context.packageName, R.layout.error_widget)
            appWidgetManager.updateAppWidget(appWidgetId, views)
            return
        }

        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.controller_widget)
        views.removeAllViews(R.id.lou_rows)

        views.setInt(R.id.lou_widget, "setBackgroundColor", controller.color)
        views.setInt(R.id.lou_rows, "setBackgroundColor", controller.color)

        views.setTextViewText(R.id.lbl_title, controller.name)
        views.setViewVisibility(R.id.lbl_title, if (showTitle) View.VISIBLE else View.GONE)

        controllerUtil.drawRemoteController(views, controller)

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)

        realm.close()
    }
}


