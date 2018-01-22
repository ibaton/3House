package treehou.se.habit.ui.homescreen

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Spinner

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import io.realm.Realm
import treehou.se.habit.R
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.ui.adapter.ServerArrayAdapter

/**
 * The configuration screen for the [VoiceControlWidget] AppWidget.
 */
class VoiceControlWidgetConfigureActivity : Activity() {

    internal var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    @BindView(R.id.spr_server) lateinit var sprServers: Spinner
    @BindView(R.id.cbx_show_title) lateinit var cbxShowTitle: CheckBox
    private var unbinder: Unbinder? = null

    internal var mOnClickListener: View.OnClickListener = View.OnClickListener {
        val context = this@VoiceControlWidgetConfigureActivity

        // It is the responsibility of the configuration view to update the app widget
        val appWidgetManager = AppWidgetManager.getInstance(context)

        //TODO check that server is selected
        saveServerPref(this@VoiceControlWidgetConfigureActivity, mAppWidgetId, sprServers.selectedItem as ServerDB, cbxShowTitle.isChecked)

        VoiceControlWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId)

        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(Activity.RESULT_CANCELED)

        setContentView(R.layout.voice_control_widget_configure)
        unbinder = ButterKnife.bind(this)

        val realm = Realm.getDefaultInstance()
        val servers = realm.where(ServerDB::class.java).findAll()
        realm.close()

        val serverAdapter = ServerArrayAdapter(this, servers)
        sprServers.adapter = serverAdapter

        findViewById<View>(R.id.add_button).setOnClickListener(mOnClickListener)

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        // If this view was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbinder!!.unbind()
    }

    companion object {
        private val PREFS_SERVER = "treehou.se.habit.ui.homescreen.VoiceControlWidget"
        private val PREF_POSTFIX_SHOW_TITLE = "_show_title"
        private val PREF_PREFIX_KEY = "appwidget_voice_"

        // Write the prefix to the SharedPreferences object for this widget
        internal fun saveServerPref(context: Context, appWidgetId: Int, server: ServerDB, showTitle: Boolean) {
            val prefs = context.getSharedPreferences(PREFS_SERVER, 0).edit()
            prefs.putLong(PREF_PREFIX_KEY + appWidgetId, server.id)
            prefs.putBoolean(PREF_PREFIX_KEY + appWidgetId + PREF_POSTFIX_SHOW_TITLE, showTitle)
            prefs.apply()
        }

        internal fun loadControllShowTitlePref(context: Context, appWidgetId: Int): Boolean {
            val prefs = context.getSharedPreferences(PREFS_SERVER, 0)
            return prefs.getBoolean(PREF_PREFIX_KEY + appWidgetId + PREF_POSTFIX_SHOW_TITLE, true)
        }

        // Read the prefix from the SharedPreferences object for this widget.
        // If there is no preference saved, get the default from a resource
        internal fun loadServerPref(context: Context, appWidgetId: Int): Long {
            val prefs = context.getSharedPreferences(PREFS_SERVER, 0)
            return prefs.getLong(PREF_PREFIX_KEY + appWidgetId, -1)
        }


        internal fun deleteTitlePref(context: Context, appWidgetId: Int) {
            val prefs = context.getSharedPreferences(PREFS_SERVER, 0).edit()
            prefs.remove(PREF_PREFIX_KEY + appWidgetId)
            prefs.apply()
        }
    }

}



