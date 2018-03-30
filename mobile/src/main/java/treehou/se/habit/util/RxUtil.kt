package treehou.se.habit.util


import android.text.TextUtils
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import treehou.se.habit.core.db.DBHelper
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.core.db.model.SitemapDB
import treehou.se.habit.core.db.model.SitemapSettingsDB
import treehou.se.habit.dagger.ServerLoaderFactory

class RxUtil {

    /**
     * Filter myopenhab servers
     * @return remove all non myopenhabservers from stream
     */
    fun filterMyOpenhabServers(): ObservableTransformer<OHServer, OHServer> {
        return ObservableTransformer { observable ->
            observable.filter({ ohServer ->
                var remoteUrl = ohServer.getRemoteUrl()
                var localUrl = ohServer.getRemoteUrl()
                remoteUrl = if (TextUtils.isEmpty(remoteUrl)) "" else remoteUrl
                localUrl = if (TextUtils.isEmpty(localUrl)) "" else localUrl

                localUrl.contains(Constants.MY_OPENHAB_URL_COMPARATOR) || remoteUrl.contains(Constants.MY_OPENHAB_URL_COMPARATOR)
            })
        }
    }

    companion object {

        fun <T> newToMainSchedulers(): ObservableTransformer<T, T> {
            return ObservableTransformer { observable ->
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
            }
        }

        /**
         * Save sitemap to database
         * @return action that saves sitemap.
         */
        fun saveSitemap(): Consumer<ServerLoaderFactory.ServerSitemapsResponse> {
            return Consumer { sitemapResponse ->
                val realm = Realm.getDefaultInstance()
                for (sitemap in sitemapResponse.sitemaps!!) {

                    val server = sitemapResponse.server
                    val serverDB = realm.where(ServerDB::class.java)
                            .equalTo("name", server!!.getName())
                            .equalTo("localurl", server.localUrl)
                            .equalTo("remoteurl", server.remoteUrl)
                            .findFirst()

                    var sitemapDB = realm.where(SitemapDB::class.java)
                            .equalTo("server.name", sitemapResponse.server!!.name)
                            .equalTo("name", sitemap.getName())
                            .findFirst()

                    if (sitemapDB == null) {
                        val sitemapSettingsDB = SitemapSettingsDB()
                        sitemapSettingsDB.display = true
                        sitemapSettingsDB.id = DBHelper.getUniqueId(realm, SitemapSettingsDB::class.java)

                        sitemapDB = SitemapDB()
                        sitemapDB.server = serverDB
                        sitemapDB.id = SitemapDB.getUniqueId(realm)
                        sitemapDB.label = sitemap.getLabel()
                        sitemapDB.link = sitemap.getLink()
                        sitemapDB.name = sitemap.getName()

                        realm.beginTransaction()
                        sitemapDB = realm.copyToRealmOrUpdate<SitemapDB>(sitemapDB)
                        realm.commitTransaction()
                    }

                    if (sitemapDB!!.settingsDB == null) {
                        var sitemapSettingsDB = SitemapSettingsDB()
                        val showSitemap = !"_default".equals(sitemapDB.name!!, ignoreCase = true)
                        sitemapSettingsDB.display = showSitemap
                        sitemapSettingsDB.id = DBHelper.getUniqueId(realm, SitemapSettingsDB::class.java)

                        realm.beginTransaction()
                        sitemapSettingsDB = realm.copyToRealmOrUpdate(sitemapSettingsDB)
                        sitemapDB.settingsDB = sitemapSettingsDB
                        realm.commitTransaction()
                    }
                }
                realm.close()
            }
        }

        /**
         * Load servers from database.
         * @return observable for generic server objects.
         */
        fun loadServers(): ObservableTransformer<Realm, OHServer> {
            return ObservableTransformer { observable ->
                observable.flatMap({ realmLocal -> realmLocal.where(ServerDB::class.java).isNotEmpty("localurl").or().isNotEmpty("remoteurl").greaterThan("id", 0).findAllAsync().asFlowable().toObservable() })
                        .flatMap({ Observable.fromIterable(it) })
                        .map({ it.toGeneric() })
                        .distinct()
            }
        }
    }
}
