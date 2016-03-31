package treehou.se.habit.connector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import retrofit.RetrofitError;
import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback;
import se.treehou.ng.ohcommunicator.services.callbacks.OHResponse;
import treehou.se.habit.core.db.settings.WidgetSettingsDB;
import treehou.se.habit.util.Util;

public class Communicator {

    private static final String TAG = "Communicator";

    private static final String MY_OPENHAB_URL = "https://my.openhab.org";

    private static Communicator mInstance;
    private Context context;
    private Map<OHServer, Picasso> requestLoaders = new HashMap<>();

    public static synchronized Communicator instance(Context context){
        if (mInstance == null) {
            mInstance = new Communicator(context);
        }
        return mInstance;
    }

    private Communicator(Context context){
        this.context = context;
    }

    public static NetworkInfo getNetworkInfo(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public static boolean isConnectedWifi(Context context){
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    private int scrubNumberValue(int number, final int min, final int max){
        return Math.max(Math.min(number, max), min);
    }

    public void incDec(final OHServer server, final OHItem item, final int value, final int min, final int max){

        OHCallback<OHItem> callback = new OHCallback<OHItem>() {
            @Override
            public void onUpdate(OHResponse<OHItem> newItem) {
                Log.d(TAG, "Item state " + newItem.body().getState() + " " + newItem.body().getType());
                String state = newItem.body().getState();
                if (treehou.se.habit.Constants.SUPPORT_INC_DEC.contains(newItem.body().getType())) {
                    if (Constants.COMMAND_OFF.equals(state) || Constants.COMMAND_UNINITIALIZED.equals(state)) {
                        if (value > 0) {
                            Openhab.instance(server).sendCommand(newItem.body().getName(), String.valueOf(scrubNumberValue(min + value, min, max)));
                        }
                    } else if (Constants.COMMAND_ON.equals(state)) {
                        if (value < 0) {
                            Openhab.instance(server).sendCommand(newItem.body().getName(), String.valueOf(scrubNumberValue(max + value, min, max)));
                        }
                    } else {
                        try {
                            int itemVal = scrubNumberValue(Integer.parseInt(newItem.body().getState()) + value, min, max);
                            Log.e(TAG, "Sending sendCommand " + itemVal + " value " + value);
                            Openhab.instance(server).sendCommand(newItem.body().getName(), String.valueOf(itemVal));
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Could not parse state " + newItem.body().getState(), e);
                        }
                    }
                }
                Openhab.instance(server).deregisterItemListener(this);
            }

            @Override
            public void onError() {
                Log.d(TAG, "incDec onError");
            }
        };
        Openhab.instance(server).requestItem(item.getName(), callback);
    }

    public Picasso buildPicasso(Context context, final OHServer server){

        if(requestLoaders.containsKey(server)){
            return requestLoaders.get(server);
        }

        OkHttpClient httpClient = TrustModifier.createAcceptAllClient();
        httpClient.interceptors().add(new Interceptor() {
            @Override
            public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {

                com.squareup.okhttp.Request.Builder newRequest = chain.request().newBuilder();
                if (server.requiresAuth()) {
                    newRequest.header(Constants.HEADER_AUTHENTICATION, ConnectorUtil.createAuthValue(server.getUsername(), server.getPassword()));
                }

                return chain.proceed(newRequest.build());
            }
        });

        final Picasso picasso = new Picasso.Builder(context)
                .downloader(new OkHttpDownloader(httpClient))
                .memoryCache(new LruCache(context))
                .build();

        requestLoaders.put(server, picasso);

        return picasso;
    }

    /**
     * Load image and put result in image view
     *
     * @param server the server credentials.
     * @param imageUrl the url of image.
     * @param imageView the view to put bitmap in.
     * @param useCache set if cache should be used.
     */
    public void loadImage(final OHServer server, final URL imageUrl, final ImageView imageView, boolean useCache){

        Log.d(TAG, "onBitmapLoaded image start " + imageUrl.toString());
        final Callback callback = new Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onBitmapLoaded image load success");
                imageView.setVisibility(View.VISIBLE);
                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

                Realm realm = Realm.getDefaultInstance();
                WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(realm);

                int imageBackground = Util.getBackground(context, bitmap, settings.getImageBackground());
                realm.close();

                imageView.setBackgroundColor(imageBackground);
            }

            @Override
            public void onError() {
                imageView.setVisibility(View.GONE);
                Log.d(TAG, "onBitmapLoaded image load failed " + imageUrl);
            }
        };

        Picasso picasso = buildPicasso(context, server);
        picasso.load(imageUrl.toString())
            .noFade()
            .placeholder(imageView.getDrawable())
            .into(imageView, callback); // problem when saving null image
    }

    /**
     * Load image and put result in image view
     *
     * @param server the server credentials.
     * @param imageUrl the url of image.
     * @param imageView the view to put bitmap in.
     */
    public void loadImage(final OHServer server, final URL imageUrl, final ImageView imageView){
        loadImage(server, imageUrl, imageView, true);
    }

    public class MultiSitemapRequest implements retrofit.Callback<List<OHSitemap>> {

        private int runningRequests;
        private SitemapsRequestListener listener;
        private OHServer server;

        public MultiSitemapRequest(int requests, final SitemapsRequestListener listener, OHServer server) {
            this.runningRequests = requests;
            this.listener = listener;
            this.server = server;
        }

        @Override
        public void success(List<OHSitemap> sitemaps, retrofit.client.Response response) {
            if(sitemaps == null) {
                sitemaps = new ArrayList<>();
            }

            for(OHSitemap sitemap : sitemaps) {
                Log.d(TAG, "Server " + server + " Sitemap " + sitemap);
                sitemap.setServer(server);
            }
            listener.onSuccess(sitemaps);
        }

        @Override
        public void failure(RetrofitError error) {
            runningRequests--;
            if(error == null){
                Log.w(TAG, "No server to connect to");
            }else {
                Log.w(TAG, "Failed to connect to server " + error.getMessage() + " " + getUrl(context, server) + " " + error.getBody());
            }

            if(runningRequests <= 0) {
                if(error != null) {
                    listener.onFailure(error.getMessage());
                }else {
                    listener.onFailure(null);
                }
            }
        }
    };

    /**
     * Get url from server.
     * @param context calling context.
     * @param server the server to connect to.
     * @return
     */
    private static String getUrl(Context context, OHServer server){
        // TODO determine if local or remote
        String url = server.getLocalUrl();
        NetworkInfo networkInfo = getNetworkInfo(context);
        if(networkInfo == null || !networkInfo.isConnected()){
            return null;
        }
        if(!isConnectedWifi(context)){
            url = server.getRemoteUrl();
        }

        return url;
    }


    public interface SitemapsRequestListener{
        void onSuccess(List<OHSitemap> sitemaps);
        void onFailure(String message);
    }
}
