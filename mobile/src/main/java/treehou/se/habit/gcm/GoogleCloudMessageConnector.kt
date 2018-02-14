package treehou.se.habit.gcm

import android.content.Context
import android.os.Build
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.gcm.GoogleCloudMessaging
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import treehou.se.habit.module.ServerLoaderFactory
import treehou.se.habit.util.ConnectionFactory
import treehou.se.habit.util.Constants
import treehou.se.habit.util.RxUtil
import java.net.URLEncoder
import javax.inject.Inject

class GoogleCloudMessageConnector @Inject
constructor() {

    @Inject lateinit var serverLoaderFactory: ServerLoaderFactory
    @Inject lateinit var realm: Realm
    @Inject lateinit var connectionFactory: ConnectionFactory

    fun registerGcm(context: Context) {
        Observable.combineLatest(
                Observable.fromCallable<String> { AdvertisingIdClient.getAdvertisingIdInfo(context).id }.subscribeOn(Schedulers.io()),
                realm.asFlowable().toObservable().compose(serverLoaderFactory.loadServersRx()).compose(RxUtil().filterMyOpenhabServers()),
                BiFunction<String, OHServer, Pair<String, OHServer>> { deviceId, ohServer -> Pair(deviceId, ohServer) })
                .filter { deviceIdOHServerPair -> !deviceIdOHServerPair.first.isEmpty()}
                .flatMap({ (deviceId, ohServer) ->
                    val gcm = GoogleCloudMessaging.getInstance(context)
                    val registrationId = gcm.register(Constants.GCM_SENDER_ID)
                    val deviceModel = URLEncoder.encode(Build.MODEL, "UTF-8")
                    connectionFactory.createServerHandler(ohServer, context).registerGcm(deviceId, deviceModel, registrationId)
                })
                .subscribe()
    }
}
