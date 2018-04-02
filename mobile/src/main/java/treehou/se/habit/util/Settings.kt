package treehou.se.habit.util

import android.content.Context
import android.content.SharedPreferences
import android.support.annotation.IntDef
import android.support.annotation.StyleRes
import android.util.SparseIntArray

import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences

import javax.inject.Inject

import io.reactivex.Observable
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap
import treehou.se.habit.R

class Settings(context: Context) {

    @Inject lateinit var preferences: SharedPreferences
    internal var rxPreferences: RxSharedPreferences

    /**
     * Get theme of application
     * @return application theme.
     */
    val themePref: Preference<Int>
    /**
     * Returns observable returning a stream of anwets if we should show sitemaps in menu.
     * @return observable emitting true if sitemaps should be shown, else false.
     */
    val showSitemapsInMenuRx: Preference<Boolean>
    /**
     * Get observable emitting items indicating if screen should be fullscreen.
     *
     * @return observable emitting true if fullscreen should be used, else false.
     */
    val fullscreenPref: Preference<Boolean>
    private val prefAutoloadSitemap: Preference<Boolean>

    /**
     * Get the sitemap to open by default
     *
     * @return default sitemap.
     */
    val defaultSitemap: String
        get() = preferences.getString(PREF_DEFAULT_SITEMAP, "")

    /**
     * Get theme of application
     * @return application theme.
     */
    /**
     * Set application theme.
     *
     * @param theme application theme
     */
    var theme: Int
        get() = themePref.get()
        set(theme) = themePref.set(theme)

    /**
     * Get theme of application
     * @return application theme.
     */
    val themeResourse: Int
        @StyleRes
        get() = getThemeResourse(themePref.get())

    /**
     * Get theme of application
     * @return application theme.
     */
    val themeRx: Observable<Int>
        get() = themePref.asObservable()

    /**
     * Get theme of application
     * @return application theme.
     */
    val themeResourceRx: Observable<Int>
        get() = themePref.asObservable().distinctUntilChanged().map { theme -> THEME_MAP.get(theme, R.style.AppTheme_Base) }

    /**
     * Check if sitemap should be autoloaded when sitemap list starts up.
     * @return get preference as rx pref.
     */
    val autoloadSitemapRx: Observable<Boolean>
        get() = prefAutoloadSitemap.asObservable()

    /**
     * Check if app has asked user to setup initial server.
     * @return true if no question has been asked. else false.
     */
    /**
     * Set if app has asked user to setup server.
     * @param isAsked true if question has been asked, else false.
     */
    var serverSetupAsked: Boolean
        get() = preferences.getBoolean(PREF_SERVER_SETUP_QUESTION, false)
        set(isAsked) {
            val editor = preferences.edit()
            editor.putBoolean(PREF_SERVER_SETUP_QUESTION, isAsked)
            editor.apply()
        }

    init {
        preferences = context.getSharedPreferences(PREF_MANAGER, Context.MODE_PRIVATE)
        rxPreferences = RxSharedPreferences.create(preferences)
        themePref = rxPreferences.getInteger(PREF_THEME, THEME_DEFAULT)
        showSitemapsInMenuRx = rxPreferences.getBoolean(PREF_SHOW_SITEMAPS_IN_MENU, true)
        fullscreenPref = rxPreferences.getBoolean(PREF_SHOW_IN_FULLSCREEN, false)
        prefAutoloadSitemap = rxPreferences.getBoolean(PREF_AUTOLOAD_SITEMAP, true)
    }

    /**
     * The user manually opened the drawer; store this flag to prevent auto-showing
     * the navigation drawer automatically in the future.
     *
     * @param learned true if user interacted with drawer, else false.
     */
    fun userLearnedDrawer(learned: Boolean) {
        preferences.edit().putBoolean(PREF_USER_LEARNED_DRAWER, learned).apply()
    }

    /**
     * Get theme of application
     * @return application theme.
     */
    @StyleRes fun getThemeResourse(@Themes theme: Int): Int {
        return THEME_MAP.get(theme, R.style.AppTheme_Base)
    }

    /**
     * Sets the sitemap to be used when logging in
     *
     * @param sitemap new default sitemap
     */
    fun setDefaultSitemap(sitemap: OHSitemap?) {
        val editor = preferences.edit()
        if (sitemap != null) {
            editor.putString(PREF_DEFAULT_SITEMAP, sitemap.name)
        } else {
            editor.remove(PREF_DEFAULT_SITEMAP)
        }
        editor.apply()
    }

    /**
     * Set if sitemap should be shown in menu.
     * @param value true to show in menu, else false.
     */
    fun setShowSitemapsInMenu(value: Boolean) {
        showSitemapsInMenuRx.set(value)
    }

    fun setAutoloadSitemapRx(autoloadSitemap: Boolean) {
        prefAutoloadSitemap.set(autoloadSitemap)
    }

    companion object {

        private val PREF_MANAGER = "treePref"

        private val PREF_DEFAULT_SITEMAP = "default_sitemap_name"
        private val PREF_THEME = "pref_them"
        private val PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned"
        private val PREF_SERVER_SETUP_QUESTION = "pref_server_setup_question"
        private val PREF_AUTOLOAD_SITEMAP = "pref_autoload_sitemap"
        private val PREF_SHOW_SITEMAPS_IN_MENU = "pref_show_sitemap_in_menu"
        private val PREF_SHOW_IN_FULLSCREEN = "pref_show_in_fullscreen"

        private val THEME_MAP = SparseIntArray()


        @IntDef(THEME_DEFAULT, THEME_HABDROID_LIGHT, THEME_HABDROID_DARK)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Themes

        const val THEME_DEFAULT = 1
        const val THEME_HABDROID_LIGHT = 2
        const val THEME_HABDROID_DARK = 3

        init {
            THEME_MAP.put(THEME_DEFAULT, R.style.AppTheme_Base)
            THEME_MAP.put(THEME_HABDROID_LIGHT, R.style.AppTheme_Base_habdroid_Light)
            THEME_MAP.put(THEME_HABDROID_DARK, R.style.AppTheme_Base_habdroid_Dark)
        }

        fun instance(context: Context): Settings {
            return Settings(context)
        }
    }
}
