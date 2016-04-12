package se.treehou.ng.ohcommunicator.connector;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import se.treehou.ng.ohcommunicator.connector.models.OHWidget;

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

    public static boolean isValidServerUrl(String url){
        return Patterns.WEB_URL.matcher(url).matches();
    }

    public static String createAuthValue(String username, String password) {

        final String credentials = username + ":" + password;
        String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        return auth;
    }

    public static String buildChartRequestString(String baseUrl, OHWidget widget){

        if(widget.getItem() == null){
            return "";
        }

        Random random = new Random();
        String type = widget.getItem().getType().equals(OHWidget.TYPE_GROUP) ? "groups" : "items";
        Uri.Builder uriBuilder = Uri.parse(baseUrl+Constants.CHART_URL).buildUpon()
                .appendQueryParameter(type, widget.getItem().getName())
                .appendQueryParameter("period", widget.getPeriod())
                .appendQueryParameter("random",String.valueOf(Math.abs(random.nextInt())));

        if(!TextUtils.isEmpty(widget.getService())){
            uriBuilder.appendQueryParameter("service", widget.getService());
        }
        Uri builtUri = uriBuilder.build();
        builtUri = changeHostUrl(builtUri, Uri.parse(widget.getItem().getLink()));

        Log.d(TAG, "Creating chart url " + builtUri.toString());

        return builtUri.toString();
    }
}
