package treehou.se.habit.ui.homescreen;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import se.treehou.ng.ohcommunicator.core.OHServerWrapper;
import treehou.se.habit.R;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link VoiceControlWidgetConfigureActivity VoiceControlWidgetConfigureActivity}
 */
public class VoiceControlWidget extends AppWidgetProvider {

    private static final String TAG = "VoiceControlWidget";

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
            VoiceControlWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
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

        boolean showTitle = VoiceControlWidgetConfigureActivity.loadControllShowTitlePref(context, appWidgetId);
        OHServerWrapper server = OHServerWrapper.load(VoiceControlWidgetConfigureActivity.loadServerPref(context, appWidgetId));

        if(server == null){
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.error_widget);
            appWidgetManager.updateAppWidget(appWidgetId, views);
            return;
        }

        Log.d(TAG, "Server " + VoiceControlWidgetConfigureActivity.loadServerPref(context, appWidgetId));

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.voice_control_widget);

        views.setTextViewText(R.id.lbl_title, server.getName());
        views.setViewVisibility(R.id.lbl_title, showTitle? View.VISIBLE:View.GONE);

        Intent intent = createVoiceCommand(context, server);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 9, intent, 0);

        views.setOnClickPendingIntent(R.id.lou_voice, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static Intent createVoiceCommand(Context context, OHServerWrapper server){

        Intent callbackIntent = VoiceService.createVoiceCommand(context, server);
        PendingIntent openhabPendingIntent = PendingIntent.getService(context.getApplicationContext(), (int)(Math.random()*Integer.MAX_VALUE), callbackIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, VoiceService.class.getPackage().getName());
        // Display an hint to the user about what he should say.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, context.getString(R.string.voice_command_title));

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(RecognizerIntent.EXTRA_RESULTS_PENDINGINTENT, openhabPendingIntent);

        return intent;
    }
}
