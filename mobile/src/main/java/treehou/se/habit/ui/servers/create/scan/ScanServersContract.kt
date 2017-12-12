package treehou.se.habit.ui.servers.create.custom

import treehou.se.habit.mvp.BasePresenter
import treehou.se.habit.mvp.BaseView

interface ScanServersContract {

    interface View : BaseView<Presenter> {
    }

    interface Presenter : BasePresenter {
    }
}
