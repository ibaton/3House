package treehou.se.habit.ui.sitemaps.sitemap


import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.mvp.BasePresenter
import treehou.se.habit.mvp.BaseView

interface SitemapContract {

    interface View : BaseView<Presenter> {
        fun showPage(server: ServerDB, page: OHLinkedPage)
        fun removeAllPages(): Boolean
        fun hasPage(): Boolean
    }

    interface Presenter : BasePresenter {

        fun showPage(page: OHLinkedPage)

        companion object {
            val ARG_SITEMAP = "ARG_SITEMAP"
            val ARG_SERVER = "ARG_SERVER"
        }
    }
}
