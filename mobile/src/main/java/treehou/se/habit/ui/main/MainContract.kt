package treehou.se.habit.ui.main


import se.treehou.ng.ohcommunicator.connector.models.OHSitemap
import treehou.se.habit.mvp.BasePresenter
import treehou.se.habit.mvp.BaseView

interface MainContract {

    interface View : BaseView<Presenter> {
        fun openSitemaps()
        fun openSitemaps(defaultSitemap: String)
        fun openSitemap(ohSitemap: OHSitemap)
        fun openControllers()
        fun openServers()
        fun openSettings()
        fun hasOpenPage(): Boolean
    }

    interface Presenter : BasePresenter {
        fun showSitemaps()
        fun showSitemap(ohSitemap: OHSitemap)
        fun showControllers()
        fun showServers()
        fun showSettings()
    }
}
