package treehou.se.habit.ui.servers.create.custom

import se.treehou.ng.ohcommunicator.connector.models.OHServer
import treehou.se.habit.mvp.BasePresenter
import treehou.se.habit.mvp.BaseView

interface ScanServersContract {

    interface View : BaseView<Presenter> {
        fun closeWindow()
    }

    interface Presenter : BasePresenter {
        fun saveServer(server: OHServer)
    }
}
