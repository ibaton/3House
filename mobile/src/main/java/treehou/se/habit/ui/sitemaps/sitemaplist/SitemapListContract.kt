package treehou.se.habit.ui.sitemaps.sitemaplist

import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap
import treehou.se.habit.mvp.BasePresenter
import treehou.se.habit.mvp.BaseView

interface SitemapListContract {

    interface View : BaseView<Presenter> {
        fun showSitemap(server: OHServer, sitemap: OHSitemap)
        fun clearList()
        fun hideEmptyView()
        fun showServerError(server: OHServer, error: Throwable)
        fun populateSitemaps(server: OHServer, sitemaps: List<OHSitemap>)
    }

    interface Presenter : BasePresenter {
        fun openSitemap(server: OHServer, sitemap: OHSitemap?)
        fun reloadSitemaps(server: OHServer)
    }
}
