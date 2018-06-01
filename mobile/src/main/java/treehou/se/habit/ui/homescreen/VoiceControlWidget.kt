package treehou.se.habit.ui.homescreen

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.view.View
import android.widget.RemoteViews

import io.realm.Realm
import treehou.se.habit.R
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.service.VoiceService

/**
 * Implementation of App WidgetFactory functionality.
 * App WidgetFactory Configuration implemented in [VoiceControlWidgetConfigureActivity]
 */
class VoiceControlWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            VoiceControlWidgetConfigureActivity.deleteTitlePref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        private val TAG = "VoiceControlWidget"

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {

            val showTitle = VoiceControlWidgetConfigureActivity.loadControllShowTitlePref(context, appWidgetId)
            val realm = Realm.getDefaultInstance()
            val server = ServerDB.load(realm, VoiceControlWidgetConfigureActivity.loadServerPref(context, appWidgetId))
            if (server == null) {
                realm.close()
                val views = RemoteViews(context.packageName, R.layout.error_widget)
                appWidgetManager.updateAppWidget(appWidgetId, views)
                return
            }

            val views = RemoteViews(context.packageName, R.layout.voice_control_widget)

            views.setTextViewText(R.id.lbl_title, server.name)
            views.setViewVisibility(R.id.lbl_title, if (showTitle) View.VISIBLE else View.GONE)

            val intent = createVoiceCommand(context, server)
            val pendingIntent = PendingIntent.getActivity(context, 9, intent, 0)

            views.setOnClickPendingIntent(R.id.lou_voice, pendingIntent)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
            realm.close()
        }

        fun createVoiceCommand(context: Context, server: ServerDB): Intent {
            val openhabPendingIntent = VoiceService.createPendingVoiceCommand(context, server, (Math.random() * Integer.MAX_VALUE).toInt())

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            // Specify the calling package to identify your application
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, VoiceService::class.java.`package`.name)
            // Display an hint to the user about what he should say.
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, context.getString(R.string.voice_command_title))

            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            //intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            intent.putExtra(RecognizerIntent.EXTRA_RESULTS_PENDINGINTENT, openhabPendingIntent)

            return intent
        }
    }
}
