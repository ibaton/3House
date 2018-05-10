package treehou.se.habit.ui.sitemaps.page


import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHWidget
import treehou.se.habit.mvp.BasePresenter
import treehou.se.habit.mvp.BaseView

interface PageContract {

    interface View : BaseView<Presenter> {
        fun showLostServerConnectionMessage()
        fun closeView()
        fun updatePage(page: OHLinkedPage)
        fun setWidgets(widgets: List<OHWidget>)
    }

    interface Presenter : BasePresenter

    companion object {
        val ARG_PAGE = "ARG_PAGE"
        val ARG_SERVER = "ARG_SERVER"
        val STATE_PAGE = "STATE_PAGE"
    }
}
