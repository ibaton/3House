package treehou.se.habit.ui.sitemaps.page


import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import treehou.se.habit.mvp.BasePresenter
import treehou.se.habit.mvp.BaseView
import treehou.se.habit.ui.widgets.WidgetFactory

interface PageContract {

    interface View : BaseView<Presenter> {
        fun showLostServerConnectionMessage()
        fun closeView()
        fun updatePage(page: OHLinkedPage)
        fun setWidgets(widgets: List<WidgetFactory.IWidgetHolder>)
    }

    interface Presenter : BasePresenter

    companion object {
        val ARG_PAGE = "ARG_PAGE"
        val ARG_SERVER = "ARG_SERVER"
        val STATE_PAGE = "STATE_PAGE"
    }
}
