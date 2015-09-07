package treehou.se.habit.connector;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import treehou.se.habit.core.Widget;
import treehou.se.habit.core.db.ItemDB;

public class ConnectorUtil {

    private static final String TAG = "ConnectorUtil";

    public static String buildChartRequestString(String baseUrl, Widget widget){

        if(widget.getItem() == null){
            return "";
        }

        Random random = new Random();
        String type = widget.getItem().getType().equals(ItemDB.TYPE_GROUP) ? "groups" : "items";
        Uri.Builder uriBuilder = Uri.parse(widget.getBaseUrl()+Constants.CHART_URL).buildUpon()
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
