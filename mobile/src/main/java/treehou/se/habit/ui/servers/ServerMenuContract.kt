package treehou.se.habit.ui.servers

import treehou.se.habit.mvp.BasePresenter
import treehou.se.habit.mvp.BaseView

interface ServerMenuContract {

    interface View : BaseView<Presenter> {
        fun openEditMyOpenhabServerPage(serverId: Long)
        fun openEditServerPage(serverId: Long)
    }

    interface Presenter : BasePresenter {
        fun editServerClicked(serverId: Long)
    }
}
