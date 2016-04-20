package treehou.se.habit.util;

import android.content.Context;
import android.content.SharedPreferences;

import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;

public class Settings {

    private static final String PREF_MANAGER = "treePref";

    private static final String PREF_DEFAULT_SITEMAP = "default_sitemap";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    private SharedPreferences preferences;

    public Settings(Context context) {
        preferences = context.getSharedPreferences(PREF_MANAGER, Context.MODE_PRIVATE);
    }

    public static Settings instance(Context context){
        return new Settings(context);
    }

    /**
     * The user manually opened the drawer; store this flag to prevent auto-showing
     * the navigation drawer automatically in the future.
     *
     * @param learned true if user interacted with drawer, else false.
     */
    public void userLearnedDrawer(boolean learned) {
        preferences.edit().putBoolean(PREF_USER_LEARNED_DRAWER, learned).apply();
    }

    /**
     * Get the sitemap to open by default
     *
     * @return default sitemap.
     */
    public String getDefaultSitemap(){
        return preferences.getString(PREF_DEFAULT_SITEMAP, "");
    }

    /**
     * Sets the sitemap to be used when logging in
     *
     * @param sitemap new default sitemap
     */
    public void setDefaultSitemap(OHSitemap sitemap){
        SharedPreferences.Editor editor = preferences.edit();
        if(sitemap != null) {
            editor.putString(PREF_DEFAULT_SITEMAP, sitemap.getName());
        } else {
            editor.remove(PREF_DEFAULT_SITEMAP);
        }
        editor.apply();
    }
}
