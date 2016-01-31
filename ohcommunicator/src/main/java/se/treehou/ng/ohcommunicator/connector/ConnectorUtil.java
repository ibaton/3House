package se.treehou.ng.ohcommunicator.connector;

import android.net.Uri;
import android.util.Base64;

import java.net.MalformedURLException;
import java.net.URL;

public class ConnectorUtil {

    private static final String TAG = "ConnectorUtil";

    public static String getBaseUrl(String link){
        try {
            URL url = new URL(link);
            return new URL(url.getProtocol(), url.getHost(), url.getPort(), "").toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Uri changeHostUrl(Uri baseUrl, Uri newHost){
        Uri.Builder builder = baseUrl.buildUpon()
                .scheme(newHost.getScheme())
                .encodedAuthority(newHost.getHost() + (newHost.getPort() != -1 ? ":" + newHost.getPort() : ""));
        return builder.build();
    }

    public static String createAuthValue(String username, String password) {

        final String credentials = username + ":" + password;
        String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        return auth;
    }

}
