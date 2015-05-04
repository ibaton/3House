package treehou.se.habit.util;

import android.content.Context;
import android.content.SharedPreferences;

import treehou.se.habit.core.db.SitemapDB;

/**
 * Created by ibaton on 2015-04-12.
 */
public class PrefSettings {

    private static final String PREF_MANAGER = "treePref";

    private static final String PREF_DEFAULT_SITEMAP = "default_sitemap";

    private SharedPreferences preferences;

    public PrefSettings(Context context) {
        preferences = context.getSharedPreferences(PREF_MANAGER, Context.MODE_PRIVATE);
    }

    public static PrefSettings instance(Context context){
        return new PrefSettings(context);
    }

    public boolean getUseDefaultSitemap(){
        return preferences.getLong(PREF_DEFAULT_SITEMAP, -1) != -1;
    }

    public SitemapDB getDefaultSitemap(){
        return SitemapDB.load(SitemapDB.class, preferences.getLong(PREF_DEFAULT_SITEMAP, -1));
    }

    public void setDefaultSitemap(SitemapDB sitemap){
        SharedPreferences.Editor editor = preferences.edit();
        if(sitemap != null) {
            editor.putLong(PREF_DEFAULT_SITEMAP, sitemap.getId());
        }
        else {
            editor.remove(PREF_DEFAULT_SITEMAP);
        }
        editor.apply();
    }
}
