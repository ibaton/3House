package treehou.se.habit.util

import android.content.Context

import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException

import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

import de.duenndns.ssl.MemorizingTrustManager
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.services.Connector
import se.treehou.ng.ohcommunicator.services.IServerHandler

class ConnectionFactory(context: Context) {

    private var mtm: MemorizingTrustManager = MemorizingTrustManager(context.applicationContext)
    private var sc: SSLContext = SSLContext.getInstance("TLS")

    init {
        setupTrustManager()
    }

    fun createServerHandler(server: OHServer, context: Context?): IServerHandler {
        return Connector.ServerHandler(server, context, sc, mtm, mtm.wrapHostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier()))
    }

    private fun setupTrustManager() {

        try {
            sc.init(null, arrayOf<X509TrustManager>(mtm), java.security.SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
            HttpsURLConnection.setDefaultHostnameVerifier(
                    mtm.wrapHostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier()))

            // disable redirects to reduce possible confusion
            SSLContext.setDefault(sc)
            HttpsURLConnection.setFollowRedirects(false)
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

    }
}
