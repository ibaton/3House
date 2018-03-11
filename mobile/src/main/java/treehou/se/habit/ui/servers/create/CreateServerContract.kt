package treehou.se.habit.ui.servers.create


import treehou.se.habit.mvp.BasePresenter
import treehou.se.habit.mvp.BaseView

interface CreateServerContract {

    interface View : BaseView<Presenter>

    interface Presenter : BasePresenter {
        fun saveDemoServer()
    }
}
