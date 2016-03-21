package treehou.se.habit.ui.homescreen;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import treehou.se.habit.R;
import treehou.se.habit.core.db.model.controller.ControllerDB;

/**
 * The configuration screen for the {@link ControllerWidget ControllerWidget} AppWidget.
 */
public class ControllerWidgetConfigureActivity extends AppCompatActivity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final String PREFS_NAME = "treehou.se.habit.ui.homescreen.ControllerWidget";
    private static final String PREF_PREFIX_KEY             = "appwidget_";
    private static final String PREF_POSTFIX_SHOW_TITLE     = "_show_title";

    private Spinner sprControllers;
    private CheckBox cbxShowTitle;

    public ControllerWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.controller_widget_configure);

        sprControllers = (Spinner) findViewById(R.id.spr_controller);
        List<ControllerDB> controllers = null;//OHRealm.realm().allObjects(ControllerDB.class);
        ArrayAdapter mAdapter = new ArrayAdapter<>(this
                , android.R.layout.simple_list_item_1, controllers);
        sprControllers.setAdapter(mAdapter);

        cbxShowTitle = (CheckBox) findViewById(R.id.cbx_show_title);

        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = ControllerWidgetConfigureActivity.this;

            ControllerDB controller = (ControllerDB) sprControllers.getSelectedItem();
            if(controller == null){
                Toast.makeText(ControllerWidgetConfigureActivity.this, getString(R.string.failed_save_controller), Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
                finish();
                return;
            }

            saveControllerIdPref(context, mAppWidgetId, controller, cbxShowTitle.isChecked());

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ControllerWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    // Write the prefix to the SharedPreferences object for this widget
    static void saveControllerIdPref(Context context, int appWidgetId, ControllerDB controller, boolean showTitle) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putLong(PREF_PREFIX_KEY + appWidgetId, controller.getId());
        prefs.putBoolean(PREF_PREFIX_KEY + appWidgetId + PREF_POSTFIX_SHOW_TITLE, showTitle);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static long loadControllIdPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getLong(PREF_PREFIX_KEY + appWidgetId, -1);
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static boolean loadControllShowTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(PREF_PREFIX_KEY + appWidgetId + PREF_POSTFIX_SHOW_TITLE, true);
    }

    static void deletePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.remove(PREF_PREFIX_KEY + appWidgetId + PREF_POSTFIX_SHOW_TITLE);
        prefs.apply();
    }
}



