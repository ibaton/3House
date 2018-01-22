package treehou.se.habit.ui.servers.create.myopenhab

import android.content.Context
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import treehou.se.habit.R
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.module.RxPresenter
import treehou.se.habit.util.ConnectionFactory
import javax.inject.Inject

class CreateMyOpenhabPresenter
@Inject
constructor(private val view: CreateMyOpenhabContract.View) : RxPresenter(), CreateMyOpenhabContract.Presenter {

    @Inject lateinit var connectionFactory: ConnectionFactory
    @Inject lateinit var analytics: FirebaseAnalytics
    @Inject lateinit var context: Context

    override fun login(username: String, password: String) {
        Log.d(TAG, "Attempting Login")

        val server = OHServer()
        server.name = "My openHAB"
        server.username = username
        server.password = password
        server.remoteUrl = MY_OPENHAB_URL
        server.setLocalurl(MY_OPENHAB_URL)

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
        realm.copyToRealmOrUpdate(serverDB)
        realm.commitTransaction()
        realm.close()
    }

    companion object {
        val TAG = CreateMyOpenhabPresenter::class.java.simpleName
        val MY_OPENHAB_URL = "https://myopenhab.org:443"
    }
}
