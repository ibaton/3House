package treehou.se.habit.ui.sitemaps.sitemap


import android.content.Context
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.dagger.RxPresenter
import treehou.se.habit.util.ConnectionFactory
import treehou.se.habit.util.logging.Logger
import javax.inject.Inject

class SitemapPresenter @Inject
constructor() : RxPresenter(), SitemapContract.Presenter {

    @Inject lateinit var view: SitemapContract.View
    @Inject @JvmField var server: ServerDB? = null
    @Inject @JvmField var sitemap: OHSitemap? = null
    @Inject lateinit var context: Context
    @Inject lateinit var log: Logger
    @Inject lateinit var connectionFactory: ConnectionFactory

    override fun showPage(page: OHLinkedPage) {
        Log.d(TAG, "Received page " + page)
        val server = server
        if (server != null) {
            view.showPage(server, page)
        }
    }

    override fun subscribe() {
        super.subscribe()

        if (sitemap == null) {
            view.removeAllPages()
            return
        }

        if (!view.hasPage()) {
            val serverHandler = connectionFactory.createServerHandler(sitemap!!.server, context)
            serverHandler.requestPageRx(sitemap!!.homepage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { this.showPage(it) },
                            { log.e(TAG, "Received page failed", it) }
                    )
        }

        EventBus.getDefault().register(this)
    }

    override fun unsubscribe() {
        super.unsubscribe()
        EventBus.getDefault().unregister(this)
    }

    /**
     * User requested to move to new page.
     *
     * @param event
     */
    @Subscribe
    fun onEvent(event: OHLinkedPage) {
        showPage(event)
    }

    companion object {

        private val TAG = SitemapPresenter::class.java.simpleName
    }
}
