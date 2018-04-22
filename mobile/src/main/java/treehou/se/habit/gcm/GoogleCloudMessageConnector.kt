package treehou.se.habit.gcm

import android.content.Context
import android.os.Build
import android.util.Log
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.gcm.GoogleCloudMessaging
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import treehou.se.habit.dagger.ServerLoaderFactory
import treehou.se.habit.util.ConnectionFactory
import treehou.se.habit.util.Constants
import treehou.se.habit.util.RxUtil
import treehou.se.habit.util.logging.Logger
import java.net.URLEncoder
import javax.inject.Inject

class GoogleCloudMessageConnector @Inject
constructor() {

    @Inject lateinit var serverLoaderFactory: ServerLoaderFactory
    @Inject lateinit var realm: Realm
    @Inject lateinit var connectionFactory: ConnectionFactory
    @Inject lateinit var logger: Logger

    fun registerGcm(context: Context) {
        Observable.combineLatest(
                Observable.fromCallable<String> { AdvertisingIdClient.getAdvertisingIdInfo(context).id }.subscribeOn(Schedulers.io()),
                realm.asFlowable().toObservable().compose(serverLoaderFactory.loadServersRx()).compose(RxUtil().filterMyOpenhabServers()),
                BiFunction<String, OHServer, Pair<String, OHServer>> { deviceId, ohServer -> Pair(deviceId, ohServer) })
                .filter { deviceIdOHServerPair -> !deviceIdOHServerPair.first.isEmpty()}
                .distinct( {(_, server) -> (server.username.hashCode() + server.password.hashCode()) } )
                .flatMap({ (deviceId, ohServer) ->
                    registerGcmForClient(context, deviceId, ohServer);
                })
                .subscribe({},{ logger.e(TAG, "Failed to register GCM", it) })
    }

    /**
     * Filter myopenhab servers
     * @return remove all non myopenhabservers from stream
     */
    private fun registerGcmForClient(context: Context, deviceId: String, ohServer: OHServer): Observable<String> {
        try {
            Log.d(TAG, "Register GCM for user ${ohServer.username}")
            val gcm = GoogleCloudMessaging.getInstance(context)
            val registrationId = gcm.register(Constants.GCM_SENDER_ID)
            val deviceModel = URLEncoder.encode(Build.MODEL, "UTF-8")
            return connectionFactory.createServerHandler(ohServer, context).registerGcm(deviceId, deviceModel, registrationId)
        }catch (_: Exception){
            return Observable.empty<String>()
        }
    }

    companion object {
        val TAG =  GoogleCloudMessageConnector::class.java.simpleName;
    }
}
