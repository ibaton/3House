package treehou.se.habit


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.crashlytics.android.Crashlytics
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import treehou.se.habit.util.Constants
import treehou.se.habit.util.Settings
import treehou.se.habit.util.logging.Logger
import javax.inject.Inject

@SuppressLint("Registered")
open class BaseActivity : RxAppCompatActivity() {

    @Inject lateinit var settings: Settings
    @Inject lateinit var logger: Logger

    protected lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as HabitApplication).component().inject(this)
        super.onCreate(savedInstanceState)
        Crashlytics.setString(Constants.FIREABASE_DEBUG_KEY_ACTIVITY, javaClass.name)
        realm = Realm.getDefaultInstance()
    }

    override fun onResume() {
        super.onResume()
        setupFullscreenHandler()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    /**
     * Set up full screen handler.
     * Will automatically switch to fullscreen when set in settings
     */
    private fun setupFullscreenHandler() {
        settings.fullscreenPref.asObservable()
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ fullscreen ->
                    showFullscreen(fullscreen)
                }) { logger.e(TAG, "Failed to set fullscreen mode", it) }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        showFullscreen(settings.fullscreenPref.get())
    }

    /**
     * Set if view should be in fullscreen.
     *
     * @param fullscreen true to set into fullscreen, else false.
     */
    private fun showFullscreen(fullscreen: Boolean) {
        if (fullscreen) {
            showFullscreen()
        } else {
            showNormal()
        }
    }

    /**
     * Hides system ui using immersive layout.
     */
    private fun showFullscreen() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    /**
     * Put system ui into normal mode.
     * This is done by default
     */
    private fun showNormal() {
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    companion object {
        private val TAG = BaseActivity::class.java.simpleName
    }
}
