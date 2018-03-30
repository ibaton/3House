package treehou.se.habit.util

import android.content.Context
import android.util.Log
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap
import treehou.se.habit.core.db.OHRealm
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.core.db.model.SitemapDB
import treehou.se.habit.dagger.ServerLoaderFactory
import java.util.*
import javax.inject.Inject

class DatabaseServerLoaderFactory @Inject
constructor(private val realm: OHRealm, private val connectionFactory: ConnectionFactory) : ServerLoaderFactory {

    override fun loadServer(realm: Realm, id: Long): OHServer {
        return ServerDB.load(realm, id)!!.toGeneric()
    }

    override fun loadServersRx(): ObservableTransformer<Realm, OHServer> {
        return RxUtil.loadServers()
    }

    override fun loadAllServersRx(): ObservableTransformer<Realm, List<OHServer>> {

        return ObservableTransformer { observable ->
            observable.flatMap({ realmLocal ->
                realmLocal.where(ServerDB::class.java).isNotEmpty("localurl").or().isNotEmpty("remoteurl").greaterThan("id", 0).findAllAsync()
                        .asFlowable().toObservable()
            })
                    .map({ serverDBS ->
                        serverDBS.map { it.toGeneric() }
                    })
                    .distinct()
        }
    }

    override fun serversToSitemap(context: Context?): ObservableTransformer<List<OHServer>, List<ServerLoaderFactory.ServerSitemapsResponse>> {
        return ObservableTransformer { observable ->
            observable
                    .switchMap({ servers ->
                        val sitemapResponseRx = ArrayList<Observable<ServerLoaderFactory.ServerSitemapsResponse>>()
                        for (server in servers) {
                            val serverSitemapsResponseRx = Observable.just<OHServer>(server)
                                    .compose<ServerLoaderFactory.ServerSitemapsResponse>(serverToSitemap(context!!))
                                    .subscribeOn(Schedulers.io())
                                    .startWith(ServerLoaderFactory.Companion.EMPTY_RESPONSE)

                            sitemapResponseRx.add(serverSitemapsResponseRx)
                        }

                        Observable.combineLatest<ServerLoaderFactory.ServerSitemapsResponse, List<ServerLoaderFactory.ServerSitemapsResponse>>(sitemapResponseRx) { responsesObject ->
                            val responses = ArrayList<ServerLoaderFactory.ServerSitemapsResponse>()
                            for (responseObject in responsesObject) {
                                val response = responseObject as ServerLoaderFactory.ServerSitemapsResponse
                                responses.add(response)
                            }

                            responses
                        }
                    })
        }
    }

    /**
     * Fetches sitemaps from server.
     * @param context the used to fetch sitemaps.
     * @return
     */
    override fun serverToSitemap(context: Context): ObservableTransformer<OHServer, ServerLoaderFactory.ServerSitemapsResponse> {
        return ObservableTransformer { observable ->
            observable
                    .flatMap({ server ->
                        val serverHandler = connectionFactory.createServerHandler(server, context)
                        serverHandler.requestSitemapRx()
                                .map { SitemapResponse(it) }
                                .subscribeOn(Schedulers.io())
                                .doOnError { e -> Log.e(TAG, "Failed to load sitemap", e) }
                                .onErrorReturn { throwable -> SitemapResponse(ArrayList(), throwable) }
                    }, { server, sitemapResponse ->
                        for (sitemap in sitemapResponse.sitemaps) {
                            sitemap.setServer(server)
                        }
                        ServerLoaderFactory.ServerSitemapsResponse(server, sitemapResponse.sitemaps, sitemapResponse.error)
                    })
                    .doOnNext(RxUtil.saveSitemap())
        }
    }

    override fun filterDisplaySitemaps(): ObservableTransformer<ServerLoaderFactory.ServerSitemapsResponse, ServerLoaderFactory.ServerSitemapsResponse> {
        return ObservableTransformer { observable -> observable.map(Function<ServerLoaderFactory.ServerSitemapsResponse, ServerLoaderFactory.ServerSitemapsResponse> { this.filterDisplaySitemaps(it) }) }
    }

    override fun filterDisplaySitemapsList(): ObservableTransformer<List<ServerLoaderFactory.ServerSitemapsResponse>, List<ServerLoaderFactory.ServerSitemapsResponse>> {
        return ObservableTransformer { observable ->
            observable.map({ serverSitemapsResponses: List<ServerLoaderFactory.ServerSitemapsResponse> ->
                val responses = ArrayList<ServerLoaderFactory.ServerSitemapsResponse>()
                for (sitemapsResponse in serverSitemapsResponses) {
                    responses.add(filterDisplaySitemaps(sitemapsResponse))
                }
                responses
            })
        }
    }

    private fun filterDisplaySitemaps(response: ServerLoaderFactory.ServerSitemapsResponse): ServerLoaderFactory.ServerSitemapsResponse {
        val sitemaps = ArrayList<OHSitemap>()
        for (sitemap in response.sitemaps!!) {
            val sitemapDB = realm.realm().where(SitemapDB::class.java)
                    .equalTo("name", sitemap.name)
                    .equalTo("server.name", response.server!!.name)
                    .findFirst()

            if (sitemapDB == null || sitemapDB.settingsDB == null
                    || sitemapDB.settingsDB!!.display) {

                sitemaps.add(sitemap)
            }
        }
        return ServerLoaderFactory.ServerSitemapsResponse(response.server, sitemaps, response.error)
    }

    private class SitemapResponse {
        var error: Throwable? = null
        var sitemaps: List<OHSitemap>

        constructor(sitemaps: List<OHSitemap>) {
            this.sitemaps = sitemaps
        }

        constructor(sitemaps: List<OHSitemap>, error: Throwable) {
            this.error = error
            this.sitemaps = sitemaps
        }
    }

    companion object {

        private val TAG = DatabaseServerLoaderFactory::class.java.simpleName
    }
}
