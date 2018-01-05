package treehou.se.habit.ui.homescreen

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.util.Log

import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap
import treehou.se.habit.BaseActivity
import treehou.se.habit.R
import treehou.se.habit.core.db.model.SitemapDB
import treehou.se.habit.ui.adapter.SitemapAdapter
import treehou.se.habit.ui.sitemaps.SitemapSelectorFragment

/**
 * The configuration screen for the [SitemapWidget] AppWidget.
 */
class SitemapWidgetConfigureActivity : BaseActivity(), SitemapAdapter.OnSitemapSelectListener {

    internal var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(Activity.RESULT_CANCELED)

        setContentView(R.layout.sitemap_widget_configure)

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        val fragmentManager = supportFragmentManager
        if (fragmentManager.findFragmentById(R.id.container) == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.container, SitemapSelectorFragment.newInstance())
                    .commit()
        }
    }

    override fun onSitemapSelect(sitemap: OHSitemap) {
        saveSitemapIdPref(this@SitemapWidgetConfigureActivity, mAppWidgetId, sitemap)

        // It is the responsibility of the configuration view to update the app widget
        val appWidgetManager = AppWidgetManager.getInstance(this@SitemapWidgetConfigureActivity)
        SitemapWidget.updateAppWidget(this@SitemapWidgetConfigureActivity, appWidgetManager, mAppWidgetId)

        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

    override fun onErrorClicked(server: OHServer) {}

    companion object {

        private val TAG = "SitemapWidgetConfigure"
        private val VOLLEY_TAG_SITEMAPS = "SitemapListFragmentSitemaps2"
        private val PREFS_NAME = "treehou.se.habit.ui.homescreen.SitemapWidget"
        private val PREF_PREFIX_KEY = "appwidget_"

        // Write the prefix to the SharedPreferences object for this widget
        internal fun saveSitemapIdPref(context: Context, appWidgetId: Int, sitemap: OHSitemap) {

            /*sitemap.saveServer();
        Log.d(TAG, "saveSitemapIdPref " + sitemap.getId());
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putLong(PREF_PREFIX_KEY + appWidgetId, sitemap.getId());
        prefs.apply();*/
        }

        // Read the prefix from the SharedPreferences object for this widget.
        // If there is no preference saved, get the default from a resource
        internal fun loadSitemap(context: Context, appWidgetId: Int): OHSitemap? {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val sitemapId = prefs.getLong(PREF_PREFIX_KEY + appWidgetId, -1)
            Log.d(TAG, "loadSitemap " + sitemapId)

            return null // OHSitemap.load(sitemapId);
        }

        internal fun deletePref(context: Context, appWidgetId: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.remove(PREF_PREFIX_KEY + appWidgetId)
            prefs.apply()
        }
    }
}

