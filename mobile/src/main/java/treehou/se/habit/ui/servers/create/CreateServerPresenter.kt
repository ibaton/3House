package treehou.se.habit.ui.servers.create

import android.content.Context
import android.net.Uri
import android.os.Bundle
import io.realm.Realm
import treehou.se.habit.R
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.ui.servers.create.custom.ServerData
import javax.inject.Inject

class CreateServerPresenter
@Inject
constructor(private val view: CreateServerContract.View, private val realm: Realm) : CreateServerContract.Presenter {

    @Inject lateinit var context: Context

    override fun load(launchData: Bundle?, savedData: Bundle?) {}


    override fun subscribe() {

    }

    override fun unsubscribe() {

    }

    private fun toUrl(text: String): String {

        val uri = Uri.parse(text)
        return uri.toString()
    }

    override fun saveDemoServer() {
        val server = ServerData(context.getString(R.string.demo) , toUrl(DEMO_OPENHAB_URL), toUrl(DEMO_OPENHAB_URL))
        saveServer(server)
    }


     private fun saveServer(serverData: ServerData) {
        val server = convertServerDataToDB(serverData)

        realm.executeTransaction { realm1 ->
            realm1.copyToRealmOrUpdate(server)
        }
        realm.close()
    }

    private fun convertServerDataToDB(serverData: ServerData): ServerDB {
        val server = ServerDB()
        server.id = ServerDB.uniqueId
        server.name = serverData.name
        server.localurl = serverData.localUrl
        server.remoteurl = serverData.remoteUrl
        server.username = serverData.username
        server.password = serverData.password
        return server
    }

    override fun save(savedData: Bundle?) {

    }

    override fun unload() {
        realm.close()
    }

    companion object {

        private val TAG = CreateServerPresenter::class.java.simpleName
        private val DEMO_OPENHAB_URL = "https://demo.openhab.org:8443"
    }
}
