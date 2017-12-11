package treehou.se.habit.ui.servers.create.myopenhab

import treehou.se.habit.mvp.BasePresenter
import treehou.se.habit.mvp.BaseView

interface CreateMyOpenhabContract {

    interface View : BaseView<Presenter> {
        fun showError(error: String)
        fun closeWindow()
    }

    interface Presenter : BasePresenter {
        fun login(username: String, password: String)
    }
}
