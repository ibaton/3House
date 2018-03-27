package treehou.se.habit.ui.servers.create.custom

import android.os.Bundle
import io.realm.Realm
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.dagger.RxPresenter
import javax.inject.Inject

class SetupServerPresenter
@Inject
constructor(private val view: SetupServerContract.View) : RxPresenter(), SetupServerContract.Presenter {

    @Inject lateinit var realm: Realm
    var serverId: Long = -1

    override fun saveServer(serverData: ServerData) {
        val server = convertServerDataToDB(serverData)

        realm.executeTransaction { realm1 ->
            realm1.copyToRealmOrUpdate(server)
        }
        realm.close()
        view.closeWindow()
    }

    private fun convertServerDataToDB(serverData: ServerData): ServerDB {
        val server = ServerDB()
        if (serverId <= 0) {
            server.id = ServerDB.uniqueId
            serverId = server.id
        } else {
            server.id = serverId
        }
        server.name = serverData.name
        server.localurl = serverData.localUrl
        server.remoteurl = serverData.remoteUrl
        server.username = serverData.username
        server.password = serverData.password
        return server
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
        }
    }

    companion object {
        private val EXTRA_SERVER_ID = "EXTRA_SERVER_ID"
        val ARG_SERVER = "ARG_SERVER"
    }
}
