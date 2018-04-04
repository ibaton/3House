package treehou.se.habit.connector;

import android.util.Log;

import java.net.*;
import javax.net.ssl.*;
import java.security.*;
import java.security.cert.*;

import okhttp3.OkHttpClient;

public class TrustModifier {

    private static final String TAG = "TrustModifier";

    private static final TrustingHostnameVerifier
            TRUSTING_HOSTNAME_VERIFIER = new TrustingHostnameVerifier();
    private static SSLSocketFactory factory;

    /** Call this with any HttpURLConnection, and it will
     modify the trust settings if it is an HTTPS connection. */
    public static void relaxHostChecking(HttpURLConnection conn)
            throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

        if (conn instanceof HttpsURLConnection) {
            HttpsURLConnection httpsConnection = (HttpsURLConnection) conn;
            SSLSocketFactory factory = prepFactory(httpsConnection);
            httpsConnection.setSSLSocketFactory(factory);
            httpsConnection.setHostnameVerifier(TRUSTING_HOSTNAME_VERIFIER);
        }
    }

    public static synchronized SSLSocketFactory prepFactory(HttpsURLConnection httpsConnection)
            throws NoSuchAlgorithmException, KeyManagementException {

        if (factory == null) {
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{ new AlwaysTrustManager() }, new SecureRandom());
            factory = ctx.getSocketFactory();
        }
        return factory;
    }

    public static class NullHostNameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            Log.i(TAG, "Approving certificate for " + hostname);
            return true;
        }
    }

    public static synchronized OkHttpClient.Builder createAcceptAllClient() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        Log.d(TAG, "onBitmapLoaded getClient ");
        try {
            client.hostnameVerifier(new TrustModifier.NullHostNameVerifier());
            client.sslSocketFactory(TrustModifier.createFactory());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return client;
    }

    public static synchronized SSLSocketFactory createFactory()
            throws KeyManagementException, NoSuchAlgorithmException {

        if (factory == null) {
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{ new AlwaysTrustManager() }, new SecureRandom());
            factory = ctx.getSocketFactory();
        }
        return factory;
    }

    private static final class TrustingHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static class AlwaysTrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException { }
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException { }
        public X509Certificate[] getAcceptedIssuers() { return new java.security.cert.X509Certificate[] {}; }
    }

}