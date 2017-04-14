package treehou.se.habit.util;

import android.content.Context;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import de.duenndns.ssl.MemorizingTrustManager;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.IServerHandler;

public class ConnectionFactory {

    static String KEYSTORE_DIR = "private";
    static String KEYSTORE_FILE = "ssl___________keys6.bks";

    private MemorizingTrustManager mtm;
    private SSLContext sc;

    public ConnectionFactory(Context context) {
        setupTrustManager(context);
    }

    public IServerHandler createServerHandler(OHServer server, Context context){
        return new Connector.ServerHandler(server, context, sc, mtm, mtm.wrapHostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier()));
    }

    private void setupTrustManager(Context context){

        try {
            MemorizingTrustManager.setKeyStoreFile(KEYSTORE_DIR, KEYSTORE_FILE);
            sc = SSLContext.getInstance("TLS");
            mtm = new MemorizingTrustManager(context.getApplicationContext());
            sc.init(null, new X509TrustManager[] { mtm }, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(
                    mtm.wrapHostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier()));

            // disable redirects to reduce possible confusion
            SSLContext.setDefault(sc);
            HttpsURLConnection.setFollowRedirects(false);
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
