package treehou.se.habit.ui.sitemaps.sitemaplist

import android.content.Context
import android.os.Bundle
import android.util.Log
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.realm.Realm
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap
import treehou.se.habit.dagger.RxPresenter
import treehou.se.habit.dagger.ServerLoaderFactory
import treehou.se.habit.dagger.ServerLoaderFactory.ServerSitemapsResponse
import treehou.se.habit.util.Settings
import javax.inject.Inject
import javax.inject.Named

class SitemapListPresenter @Inject
constructor(@param:Named("arguments") var arguments: Bundle) : RxPresenter(), SitemapListContract.Presenter {

    @Inject lateinit var view: SitemapListContract.View
    @Inject lateinit var context: Context
    @Inject lateinit var settings: Settings
    @Inject lateinit var realm: Realm
    @Inject lateinit var serverLoaderFactory: ServerLoaderFactory

    private var showSitemap: String? = ""

    private val serverBehaviorSubject = BehaviorSubject.create<OHServer>()

    override fun load(launchData: Bundle?, savedData: Bundle?) {
        super.load(launchData, savedData)
        showSitemap = if (savedData != null) "" else arguments.getString(SitemapListFragment.ARG_SHOW_SITEMAP)
    }


    override fun subscribe() {
        super.subscribe()
        view.clearList()
        loadSitemapsFromServers()
    }

    override fun reloadSitemaps(server: OHServer) {
        serverBehaviorSubject.onNext(server)
    }

    /**
     * Load servers from database and request their sitemaps.
     */
    private fun loadSitemapsFromServers() {
        Observable.merge(
                realm.asFlowable().toObservable()
                        .compose(serverLoaderFactory.loadServersRx()),
                serverBehaviorSubject.toFlowable(BackpressureStrategy.DROP).toObservable())
                .doOnNext { view.hideEmptyView() }
                .observeOn(Schedulers.io())
                .compose<ServerSitemapsResponse>(serverLoaderFactory.serverToSitemap(context))
                .observeOn(AndroidSchedulers.mainThread())
                .compose<ServerSitemapsResponse>(bindToLifecycle<ServerSitemapsResponse>())
                .compose<ServerSitemapsResponse>(serverLoaderFactory.filterDisplaySitemaps())
                .subscribe(
                        { this.populateSitemap(it) },
                        { Log.e(TAG, "Request sitemap failed", it) }
                )
    }

    private fun populateSitemap(serverSitemaps: ServerSitemapsResponse) {
        val server = serverSitemaps.server
        val sitemaps = serverSitemaps.sitemaps

        if (serverSitemaps.hasError()) {
            val error = serverSitemaps.error
            if(error != null && server != null) {
                view.showServerError(server, error)
            }
        } else {
            if(sitemaps != null && server != null) {
                view.populateSitemaps(server, sitemaps)
            }

            if(sitemaps != null) {
                for (sitemap in sitemaps) {
                    if (sitemap.name == showSitemap && server != null) {
                        showSitemap = null // Prevents sitemap from being accessed again.
                        openSitemap(server, sitemap)
                    }
                }
            }
        }
    }

    override fun openSitemap(server: OHServer, sitemap: OHSitemap?) {
        settings.setDefaultSitemap(sitemap)
        view.showSitemap(server, sitemap!!)
    }

    companion object {

        private val TAG = SitemapListPresenter::class.java.simpleName
    }
}
