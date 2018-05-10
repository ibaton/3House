package treehou.se.habit.ui.sitemaps.page

import android.content.Context
import android.os.Build
import android.os.Bundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHWidget
import se.treehou.ng.ohcommunicator.util.GsonHelper
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.dagger.RxPresenter
import treehou.se.habit.dagger.ServerLoaderFactory
import treehou.se.habit.util.ConnectionFactory
import treehou.se.habit.util.RxUtil
import treehou.se.habit.util.logging.Logger
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class PagePresenter @Inject
constructor(private val view: PageContract.View, private val fragment: PageFragment, private val context: Context, @param:Named("arguments") private val args: Bundle, private val log: Logger, private val serverLoaderFactory: ServerLoaderFactory, private val connectionFactory: ConnectionFactory, private val realm: Realm) : RxPresenter(), PageContract.Presenter {

    @Inject
    lateinit var logger: Logger

    private val widgets = ArrayList<OHWidget>()
    private var initialized = false

    private var server: ServerDB? = null
    private var page: OHLinkedPage? = null


    private val dataLoadError = Consumer<Throwable> { throwable ->
        log.e(TAG, "Error when requesting page ", throwable)
        view.showLostServerConnectionMessage()
        view.closeView()
    }

    override fun load(launchData: Bundle?, savedData: Bundle?) {
        super.load(launchData, savedData)
        val gson = GsonHelper.createGsonBuilder()

        val serverId = args.getLong(PageContract.ARG_SERVER)
        var jPage = args.getString(PageContract.ARG_PAGE)

        server = ServerDB.load(realm, serverId)
        page = gson.fromJson(jPage, OHLinkedPage::class.java)

        initialized = false
        if (savedData != null && savedData.containsKey(PageContract.STATE_PAGE)) {
            jPage = savedData.getString(PageContract.STATE_PAGE)
            val savedPage = gson.fromJson(jPage, OHLinkedPage::class.java)
            if (savedPage.id == page!!.id) {
                page = savedPage
                initialized = true
            }
        }
    }


    override fun subscribe() {
        super.subscribe()
        updatePage(page, true)
        if (!initialized && server != null) {
            requestPageUpdate()
        }
        initialized = true

        // Start listening for server updates
        if (supportsLongPolling()) {
            createLongPoller()
        }
    }

    override fun save(savedData: Bundle?) {
        super.save(savedData)
        savedData!!.putSerializable(PageContract.STATE_PAGE, GsonHelper.createGsonBuilder().toJson(page))
    }

    /**
     * Check if android device supports long polling.
     * @return true if long polling is supported, else false.
     */
    private fun supportsLongPolling(): Boolean {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && server != null
    }

    /**
     * Request page from server.
     */
    private fun requestPageUpdate() {
        val serverHandler = connectionFactory.createServerHandler(server!!.toGeneric(), context)

        serverHandler.requestPageRx(page)
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError({ throwable -> logger.e(TAG, "Request Page Update", throwable) })
                .subscribe(Consumer { ohLinkedPage ->
                    log.d(TAG, "Received update " + ohLinkedPage.getWidgets().size + " widgets from  " + page!!.link)
                    updatePage(ohLinkedPage)
                }, dataLoadError)
    }

    /**
     * Create longpoller listening for updates of page.
     *
     * @return
     */
    private fun createLongPoller(): Disposable {
        val serverId = server!!.id

        val server = serverLoaderFactory.loadServer(realm, serverId)
        val serverHandler = connectionFactory.createServerHandler(server, context)
        return serverHandler.requestPageUpdatesRx(page)
                .compose(this.bindToLifecycle())
                .compose(RxUtil.newToMainSchedulers())
                .doOnError({ throwable -> logger.e(TAG, "LongPoller page request", throwable) })
                .subscribe(Consumer { this.updatePage(it) }, dataLoadError)
    }


    /**
     * Update page.
     *
     * Recreate all widgets needed.
     *
     * @param page the page to show.
     * @param force true to invalidate all widgets, false to do if needed.
     */
    @Synchronized
    private fun updatePage(page: OHLinkedPage?, force: Boolean) {
        if (page == null || page.widgets == null) return

        this.page = page
        val pageWidgets = page.widgets

        invalidateWidgets(pageWidgets)

        view.updatePage(page)
    }

    /**
     * Update page.
     * Invalidate widgets if possible.
     *
     * @param page
     */
    @Synchronized
    private fun updatePage(page: OHLinkedPage) {
        updatePage(page, false)
    }

    /**
     * Check if item can be updgraded without replacing widget.
     * @param widget1 first widget to check.
     * @param widget2 second widget to check.
     * @return true if widget can be updated, else false.
     */
    fun canBeUpdated(widget1: OHWidget, widget2: OHWidget): Boolean {
        if (widget1.type != widget2.type) {
            return false
        }

        if (widget1.item == null && widget2.item == null) {
            return true
        }

        return if (widget1.item != null && widget2.item != null) {
            widget1.item.type == widget2.item.type
        } else false

    }

    /**
     * Check if item can be updgraded without replacing widget.
     * @param widgetSet1 first widget set to check.
     * @param widgetSet2 second widget set to check.
     * @return true if widget can be updated, else false.
     */
    fun canBeUpdated(widgetSet1: List<OHWidget>, widgetSet2: List<OHWidget>): Boolean {
        val invalidate = widgetSet1.size != widgetSet2.size
        if (!invalidate) {
            for (i in widgetSet1.indices) {
                val currentWidget = widgetSet1[i]
                val newWidget = widgetSet2[i]

                // TODO check if widget needs updating
                if (!canBeUpdated(currentWidget, newWidget)) {
                    log.d(TAG, "WidgetFactory " + currentWidget.type + " " + currentWidget.label + " needs update")
                    return false
                }
            }
            return true
        }
        return false
    }

    /**
     * Invalidate all widgets in page.
     * @param pageWidgets the widgets to update.
     */
    private fun invalidateWidgets(pageWidgets: List<OHWidget>) {
        log.d(TAG, "Invalidate widgets")
        view.setWidgets(pageWidgets)
    }

    companion object {

        private val TAG = PagePresenter::class.java.simpleName
    }
}
