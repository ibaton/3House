package treehou.se.habit.ui.homescreen;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import treehou.se.habit.R;
import treehou.se.habit.ui.adapter.SitemapAdapter;
import treehou.se.habit.ui.sitemaps.SitemapSelectorFragment;

/**
 * The configuration screen for the {@link SitemapWidget SitemapWidget} AppWidget.
 */
public class SitemapWidgetConfigureActivity extends AppCompatActivity implements SitemapAdapter.OnSitemapSelectListener {

    private static final String TAG = "SitemapWidgetConfigure";
    private static final String VOLLEY_TAG_SITEMAPS = "SitemapListFragmentSitemaps2";

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final String PREFS_NAME = "treehou.se.habit.ui.homescreen.SitemapWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";

    public SitemapWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.sitemap_widget_configure);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.findFragmentById(R.id.container) == null){
            fragmentManager.beginTransaction()
                .add(R.id.container, SitemapSelectorFragment.newInstance())
                .commit();
        }
    }

    @Override
    public void onSitemapSelect(OHSitemap sitemap) {
        saveSitemapIdPref(SitemapWidgetConfigureActivity.this, mAppWidgetId, sitemap);

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(SitemapWidgetConfigureActivity.this);
        SitemapWidget.updateAppWidget(SitemapWidgetConfigureActivity.this, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveSitemapIdPref(Context context, int appWidgetId, OHSitemap sitemap) {

        /*sitemap.save();
        Log.d(TAG, "saveSitemapIdPref " + sitemap.getId());
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putLong(PREF_PREFIX_KEY + appWidgetId, sitemap.getId());
        prefs.apply();*/
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static OHSitemap loadSitemap(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        long sitemapId = prefs.getLong(PREF_PREFIX_KEY + appWidgetId, -1);
        Log.d(TAG, "loadSitemap " + sitemapId);

        return null; //OHSitemap.load(sitemapId);
    }

    static void deletePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onErrorClicked(OHServer server) {}
}

