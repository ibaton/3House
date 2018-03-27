package treehou.se.habit.ui.settings.subsettings.general

import io.reactivex.android.schedulers.AndroidSchedulers
import treehou.se.habit.dagger.RxPresenter
import treehou.se.habit.util.Settings
import javax.inject.Inject

class GeneralSettingsPresenter @Inject
constructor(private val view: GeneralSettingsContract.View) : RxPresenter(), GeneralSettingsContract.Presenter {

    @Inject lateinit var settings: Settings


    override fun subscribe() {
        super.subscribe()

        val settingsAutoloadSitemapRx = settings.autoloadSitemapRx
        settingsAutoloadSitemapRx
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { show -> view.showAutoLoadSitemap(show!!) }

        val settingsShowSitemapInMenuRx = settings.showSitemapsInMenuRx
        settingsShowSitemapInMenuRx.asObservable()
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { show -> view.showSitemapsInMenu(show) }

        val settingsFullscreenRx = settings.fullscreenPref
        settingsFullscreenRx.asObservable()
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { show -> view.setFullscreen(show!!) }
    }

    override fun themeSelected(theme: Int) {
        val themePref = settings.themePref
        if (themePref.get() != theme) {
            themePref.set(theme)
            view.updateTheme()
        }
    }

    override fun setShowSitemapsInMenu(show: Boolean) {
        settings.showSitemapsInMenuRx.set(show)
    }

    override fun setAutoLoadSitemap(show: Boolean) {
        val autoloadSitemapRx = settings.autoloadSitemapRx
        if (show != autoloadSitemapRx.blockingFirst()) {
            settings.setAutoloadSitemapRx(show)
        }
    }

    override fun setFullscreen(fullscreen: Boolean) {
        val fullscreenPrefRx = settings.fullscreenPref
        if (fullscreen != fullscreenPrefRx.get()) {
            fullscreenPrefRx.set(fullscreen)
        }
    }
}
