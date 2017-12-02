package treehou.se.habit.ui.servers.serverlist

import treehou.se.habit.mvp.BasePresenter
import treehou.se.habit.mvp.BaseView

interface ServersContract {
    interface View : BaseView<Presenter>
    interface Presenter : BasePresenter
}
