package treehou.se.habit.ui.control

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import android.view.View
import android.widget.RemoteViews

import com.mattyork.colours.Colour

import io.realm.Realm
import treehou.se.habit.R
import treehou.se.habit.core.db.model.controller.CellDB
import treehou.se.habit.core.db.model.controller.ControllerDB
import treehou.se.habit.ui.homescreen.ControllerWidget
import treehou.se.habit.util.NotificationUtil
import treehou.se.habit.util.Util

class ControllerUtil(private val context: Context, private val realm: Realm, private val cellFactory: CellFactory) {

    fun updateWidget(widgetId: Int) {
        val intent = Intent(context, ControllerWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = intArrayOf(widgetId)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        context.sendBroadcast(intent)
    }

    /**
     * Populate remote view with controller cells
     *
     * @param rows
     * @return
     */
    fun drawRemoteController(rows: RemoteViews, controller: ControllerDB): RemoteViews {

        Log.d(TAG, "Drawing remote controller")
        for (row in controller.cellRows) {
            Log.d(TAG, "Rows " + controller.cellRows.size)
            val rowView = RemoteViews(context.packageName, R.layout.homescreen_widget_row)

            for (cell in row.cells) {
                val itemView = cellFactory.createRemote(context, controller, cell)
                rowView.addView(R.id.lou_row, itemView)
            }
            rows.addView(R.id.lou_rows, rowView)
        }
        return rows
    }

    /**
     * Show remote view as notification
     */
    fun showNotification(controller: ControllerDB) {

        Log.d(TAG, "Show controller as notification")

        if (controller.showNotification) {
            val views = RemoteViews(context.packageName, R.layout.controller_widget_notification)
            views.removeAllViews(R.id.lou_rows)
            views.setInt(R.id.lou_widget, "setBackgroundColor", controller.color)
            views.setInt(R.id.lou_rows, "setBackgroundColor", controller.color)
            views.setViewVisibility(R.id.lbl_title, View.GONE)

            drawRemoteController(views, controller)

            val notification = NotificationCompat.Builder(context, NotificationUtil.CHANNEL_ID_CONTROLLERS)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentTitle(controller.name)
                    .setContentText(controller.name)
                    .setCustomBigContentView(views)
                    .build()

            NotificationManagerCompat.from(context).notify(controller.id.toInt(), notification)
        }
    }

    /**
     * Show all controllers as notifications.
     *
     * @param context
     */
    fun showNotifications(context: Context) {
        NotificationManagerCompat.from(context).cancelAll()
        for (controller in realm.where(ControllerDB::class.java).findAll()) {
            if (controller.showNotification) {
                showNotification(controller)
            }
        }
        realm.close()
    }

    /**
     * Hide controller notification
     *
     * @param controllerID
     */
    fun hideNotification(controllerID: Int) {
        NotificationManagerCompat.from(context).cancel(controllerID)
    }

    companion object {

        private val TAG = ControllerUtil::class.java.simpleName

        val INDEX_BUTTON = 0

        @JvmOverloads
        fun generateColor(controller: ControllerDB, cell: CellDB, preventInvis: Boolean = true): IntArray {
            val pallete: IntArray
            if (Colour.alpha(cell.color) < 150) {
                if (preventInvis && Colour.alpha(controller.color) < 150) {
                    pallete = Util.generatePallete(Color.LTGRAY)
                } else {
                    pallete = Util.generatePallete(controller.color)
                }
            } else {
                pallete = Util.generatePallete(cell.color)
            }

            return pallete
        }
    }
}
