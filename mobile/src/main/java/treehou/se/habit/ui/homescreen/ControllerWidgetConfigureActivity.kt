package treehou.se.habit.ui.homescreen

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import android.widget.Toast
import com.jakewharton.rxbinding2.widget.RxAdapterView
import kotlinx.android.synthetic.main.controller_widget_configure.*
import treehou.se.habit.BaseActivity
import treehou.se.habit.HabitApplication
import treehou.se.habit.R
import treehou.se.habit.core.db.model.controller.ControllerDB
import treehou.se.habit.ui.control.ControllerUtil
import java.util.*
import javax.inject.Inject

/**
 * The configuration screen for the [ControllerWidget] AppWidget.
 */
class ControllerWidgetConfigureActivity : BaseActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    @Inject lateinit var controllerUtil: ControllerUtil

    public override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as HabitApplication).component().inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.controller_widget_configure)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(Activity.RESULT_CANCELED)

        val controllers = realm.where(ControllerDB::class.java).findAll()
        val controllerItems = ArrayList<ControllerItem>()
        for (controllerDB in controllers) controllerItems.add(ControllerItem(controllerDB))

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, controllerItems)
        sprControllers.adapter = adapter

        RxAdapterView.itemSelections<SpinnerAdapter>(sprControllers)
                .map { index -> index != AdapterView.INVALID_POSITION }
                .subscribe({ addButton.isEnabled = true }, { Log.e(TAG, "sprControllers update failed", it) })

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        // If this view was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
        }

        addButton.setOnClickListener {
            saveWidget()
        }
    }

    internal inner class ControllerItem(val controllerDB: ControllerDB) {

        override fun toString(): String {
            return controllerDB.name ?: "ControllerWidgetConfigureActivity"
        }
    }

    private fun saveWidget() {
        val context = this@ControllerWidgetConfigureActivity
        val selectedItem: Any? = sprControllers.selectedItem
        if(selectedItem == null){
            Toast.makeText(context, "Failed to save widget", Toast.LENGTH_LONG).show()
            return
        }

        val controller: ControllerDB? = (selectedItem as ControllerItem).controllerDB
        if (controller == null) {
            Toast.makeText(this@ControllerWidgetConfigureActivity, getString(R.string.failed_save_controller), Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        saveControllerIdPref(context, appWidgetId, controller, cbxShowTitle.isChecked)

        // It is the responsibility of the configuration view to update the app widget
        val appWidgetManager = AppWidgetManager.getInstance(context)
        controllerUtil.updateWidget(appWidgetId)

        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

    companion object {
        const val TAG = "ControllerWidgetConfi"
        private val PREFS_NAME = "treehou.se.habit.ui.homescreen.ControllerWidget"
        private val PREF_PREFIX_KEY = "appwidget_"
        private val PREF_POSTFIX_SHOW_TITLE = "_show_title"

        // Write the prefix to the SharedPreferences object for this widget
        internal fun saveControllerIdPref(context: Context, appWidgetId: Int, controller: ControllerDB, showTitle: Boolean) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.putLong(PREF_PREFIX_KEY + appWidgetId, controller.id)
            prefs.putBoolean(PREF_PREFIX_KEY + appWidgetId + PREF_POSTFIX_SHOW_TITLE, showTitle)
            prefs.apply()
        }

        // Read the prefix from the SharedPreferences object for this widget.
        // If there is no preference saved, get the default from a resource
        internal fun loadControllIdPref(context: Context, appWidgetId: Int): Long {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            return prefs.getLong(PREF_PREFIX_KEY + appWidgetId, -1)
        }

        // Read the prefix from the SharedPreferences object for this widget.
        // If there is no preference saved, get the default from a resource
        internal fun loadControllShowTitlePref(context: Context, appWidgetId: Int): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            return prefs.getBoolean(PREF_PREFIX_KEY + appWidgetId + PREF_POSTFIX_SHOW_TITLE, true)
        }

        internal fun deletePref(context: Context, appWidgetId: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.remove(PREF_PREFIX_KEY + appWidgetId)
            prefs.remove(PREF_PREFIX_KEY + appWidgetId + PREF_POSTFIX_SHOW_TITLE)
            prefs.apply()
        }
    }
}



