package treehou.se.habit.connector;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import java.util.Random;

import treehou.se.habit.core.Widget;

/**
 * Created by ibaton on 2014-10-18.
 */
public class ConnectorUtil {

    private static final String TAG = "ConnectorUtil";

    public static String buildChartRequestString(String baseUrl, Widget widget){

        Random random = new Random();
        return baseUrl+String.format(Constants.CHART_URL
                , widget.getItem().getName(), widget.getPeriod(), Math.abs(random.nextInt()));
    }

    public static Uri changeHostUrl(Uri baseUrl, Uri newHost){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(newHost.getScheme())
                .encodedAuthority(newHost.getHost() + (newHost.getPort() != -1 ? ":" + newHost.getPort() : ""));
        for(String path : baseUrl.getPathSegments()){
            builder.appendPath(path);
        }
        return builder.build();
    }

    public static String createAuthValue(String username, String password) {

        final String credentials = username + ":" + password;
        return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT);
    }

}
