package treehou.se.habit.dagger

import android.content.Context

import java.util.ArrayList

import io.reactivex.ObservableTransformer
import io.realm.Realm
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap

interface ServerLoaderFactory {

    fun loadServer(realm: Realm, id: Long): OHServer
    fun loadServersRx(): ObservableTransformer<Realm, OHServer>
    fun serverToSitemap(context: Context): ObservableTransformer<OHServer, ServerSitemapsResponse>
    fun serversToSitemap(context: Context?): ObservableTransformer<List<OHServer>, List<ServerSitemapsResponse>>
    fun filterDisplaySitemaps(): ObservableTransformer<ServerSitemapsResponse, ServerSitemapsResponse>
    fun filterDisplaySitemapsList(): ObservableTransformer<List<ServerSitemapsResponse>, List<ServerSitemapsResponse>>
    fun loadAllServersRx(): ObservableTransformer<Realm, List<OHServer>>

    class ServerSitemapsResponse {
        var server: OHServer? = null
            private set
        var sitemaps: List<OHSitemap>? = null
            private set
        var error: Throwable? = null

        constructor(server: OHServer?, sitemaps: List<OHSitemap>) {
            this.server = server
            this.sitemaps = sitemaps
        }

        constructor(server: OHServer?, sitemaps: List<OHSitemap>, error: Throwable?) {
            this.server = server
            this.sitemaps = sitemaps
            this.error = error
        }

        fun hasError(): Boolean {
            return error != null
        }
    }

    companion object {

        val EMPTY_RESPONSE = ServerSitemapsResponse(null, ArrayList())
    }
}
