package treehou.se.habit.ui.servers.create.custom

import io.realm.Realm
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.dagger.RxPresenter
import javax.inject.Inject

class ScanServersPresenter
@Inject
constructor(private val view: ScanServersContract.View) : RxPresenter(), ScanServersContract.Presenter {

    override fun saveServer(server: OHServer) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val serverDB = ServerDB.fromGeneric(server)
        realm.copyToRealmOrUpdate(serverDB)
        realm.commitTransaction()
        realm.close()

        view.closeWindow()
    }
}
