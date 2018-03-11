package treehou.se.habit.ui.servers.create.myopenhab

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import treehou.se.habit.R
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.gcm.GoogleCloudMessageConnector
import treehou.se.habit.module.RxPresenter
import treehou.se.habit.util.ConnectionFactory
import treehou.se.habit.util.Constants
import javax.inject.Inject

class CreateMyOpenhabPresenter
@Inject
constructor(private val view: CreateMyOpenhabContract.View) : RxPresenter(), CreateMyOpenhabContract.Presenter {

    @Inject lateinit var connectionFactory: ConnectionFactory
    @Inject lateinit var analytics: FirebaseAnalytics
    @Inject lateinit var context: Context
    @Inject lateinit var realm: Realm
    var launchData = Bundle()
    var hasLoadedUser = false
    var serverId: Long? = null

    override fun load(launchData: Bundle?, savedData: Bundle?) {
        super.load(launchData, savedData)

        if(launchData != null){
            this.launchData = launchData
        }
    }

    override fun subscribe() {
        super.subscribe()

        if(!hasLoadedUser && launchData.containsKey(CreateMyOpenhabContract.ARG_SERVER)){
            hasLoadedUser = true
            serverId = launchData.getLong(CreateMyOpenhabContract.ARG_SERVER)
            val server = realm.where(ServerDB::class.java).equalTo("id", serverId).findFirst()

            if(server != null){
                view.loadServerName(server.name ?: "My openHAB");
                view.loadUsername(server.username ?: "");
                view.loadPassword(server.password ?: "")
            }
        }
    }

    override fun login(serverName: String, username: String, password: String) {
        Log.d(TAG, "Attempting Login")

        val server = OHServer()
        server.name = serverName
        server.username = username
        server.password = password
        server.remoteUrl = Constants.MY_OPENHAB_URL
        server.setLocalurl(Constants.MY_OPENHAB_URL)

        val createServerHandler = connectionFactory.createServerHandler(server, context)

        createServerHandler?.requestSitemapRx()
                ?.subscribeOn(Schedulers.io())
                ?.subscribe({ _ ->
                    Log.d(TAG, "Login succeded")
                    saveServer(server)
                    view.closeWindow()
                }, { _ ->
                    Log.d(TAG, "Login Failed")
                    val error = context?.getString(R.string.error_username_password)
                    if(error != null) {
                        view.showError(error)
                    }
                })
    }

    private fun saveServer(server: OHServer) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val serverDB = ServerDB.fromGeneric(server)
        val editId: Long? = serverId
        if(editId != null){
            serverDB.id = editId
        }
        serverDB.isMyOpenhabServer = true
        realm.copyToRealmOrUpdate(serverDB)
        realm.commitTransaction()
        realm.close()
    }

    companion object {
        val TAG = CreateMyOpenhabPresenter::class.java.simpleName
    }
}
