package treehou.se.habit.ui.servers.create.custom

import android.os.Bundle
import io.realm.Realm
import treehou.se.habit.R
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.module.RxPresenter
import javax.inject.Inject

class SetupServerPresenter
@Inject
constructor(private val view: SetupServerContract.View) : RxPresenter(), SetupServerContract.Presenter {

    @Inject lateinit var realm: Realm
    var serverId: Long = -1

    override fun saveServer(server: ServerDB) {
        realm.executeTransaction { realm1 ->
            realm1.copyToRealmOrUpdate(server)
        }
        realm.close()
        view.closeWindow()
    }

    override fun load(launchData: Bundle?, savedData: Bundle?) {
        super.load(launchData, savedData)

        if (savedData != null && savedData.containsKey(EXTRA_SERVER_ID)) {
            serverId = savedData.getLong(EXTRA_SERVER_ID)
        } else if (launchData != null) {
            if (launchData.containsKey(ARG_SERVER)) serverId = launchData.getLong(ARG_SERVER)
        }
    }

    override fun save(savedData: Bundle?) {
        super.save(savedData)

        savedData?.putLong(EXTRA_SERVER_ID, serverId)
    }

    override fun subscribe() {
        super.subscribe()

        val realm = Realm.getDefaultInstance()
        val server = realm.where(ServerDB::class.java).equalTo("id", serverId).findFirst()
        realm.close()

        if (server != null) {
            view.loadServer(server)
            view.showTopLabel(false)
        }
    }

    companion object {
        private val EXTRA_SERVER_ID = "EXTRA_SERVER_ID"
        val ARG_SERVER = "ARG_SERVER"
    }
}
