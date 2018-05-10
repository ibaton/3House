package treehou.se.habit.ui.main

import android.os.Bundle
import android.support.v4.text.TextUtilsCompat
import android.text.TextUtils

import javax.inject.Inject

import io.realm.Realm
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.util.Settings


open class MainPresenter @Inject
constructor(private val mainView: MainContract.View, private val realm: Realm, private val settings: Settings) : MainContract.Presenter {

    override fun load(launchData: Bundle?, savedData: Bundle?) {
        setupFragments(savedData)
    }


    override fun subscribe() {}

    override fun unsubscribe() {}

    override fun unload() {
    }

    /**
     * Setup the saved instance state.
     * @param savedInstanceState saved instance state
     */
    private fun setupFragments(savedInstanceState: Bundle?) {
        if (!mainView.hasOpenPage()) {

            // Load server setup server fragment if no server found
            val serverDBs = realm.where(ServerDB::class.java).findAll()

            if (serverDBs.size <= 0) {
                mainView.openServers()
            } else {
                // Load default sitemap if any
                val defaultSitemap = settings.defaultSitemap
                val autoloadLast = settings.autoloadSitemapRx.blockingFirst()
                if (savedInstanceState == null && !TextUtils.isEmpty(defaultSitemap) && autoloadLast) {
                    mainView.openSitemaps(defaultSitemap)
                } else {
                    mainView.openSitemaps()
                }
            }
        }
    }

    override fun save(savedData: Bundle?) {}

    override fun showSitemaps() {
        mainView.openSitemaps()
    }

    override fun showSitemap(ohSitemap: OHSitemap) {
        mainView.openSitemap(ohSitemap)
    }

    override fun showControllers() {
        mainView.openControllers()
    }

    override fun showServers() {
        mainView.openServers()
    }

    override fun showSettings() {
        mainView.openSettings()
    }

    companion object {

        private val TAG = MainPresenter::class.java.simpleName
    }
}
