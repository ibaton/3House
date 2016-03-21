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
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
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

    public static MyOpenHabService generateMyOpenHabService(OHServer server){
        try {
            return BasicAuthServiceGenerator.createService(MyOpenHabService.class, MY_OPENHAB_URL, server.getUsername(), server.getPassword());
        }
        catch (Exception e) {
            Log.e(TAG, "Failed to create OpenhabService ", e);
        }
        return null;
    }

    public static OpenHabService generateOpenHabService(OHServer server, boolean local){
        return generateOpenHabService(server, local ? server.getLocalUrl() : server.getRemoteUrl());
    }

    public static OpenHabService generateOpenHabService(OHServer server, String url){
        try {
            return BasicAuthServiceGenerator.createService(OpenHabService.class, url, server.getUsername(), server.getPassword());
        }
        catch (Exception e) {
            Log.e(TAG, "Failed to create OpenhabService ", e);
        }
        return null;
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

            }
        };
        Openhab.instance(server).registerItemListener(item.getName(), callback);
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

    public void requestSitemaps(String tag, final OHServer server, final SitemapsRequestListener listener){

        Uri url = Uri.parse("http://www.dummy.com:8080");

        boolean localRequest = server.haveLocal() && isConnectedWifi(context);
        boolean remoteRequest = server.haveRemote();

        int requests = 0;
        if(localRequest) requests++;
        if(remoteRequest) requests++;

        MultiSitemapRequest callback = new MultiSitemapRequest(requests, listener, server);

        if(localRequest) {
            final Uri localRequestUrl = ConnectorUtil.changeHostUrl(url, Uri.parse(server.getLocalUrl()));
            OpenHabService service = generateOpenHabService(server, localRequestUrl.toString());
            service.listSitemaps(callback);
            Log.d(TAG, "requestSitemaps - Fire local request " + localRequestUrl);
        }

        // Create remote request. Fallback to remote if have any
        if(remoteRequest){
            final Uri remoteRequestUrl = ConnectorUtil.changeHostUrl(url, Uri.parse(server.getRemoteUrl()));
            OpenHabService service = generateOpenHabService(server, remoteRequestUrl.toString());
            service.listSitemaps(callback);
            Log.d(TAG, "requestSitemaps - Fire remote request " + remoteRequestUrl);
        }

        if(requests <= 0){
            // TODO Throw callback
            Log.e(TAG, "requestSitemaps - Didn't do any requests");
            callback.failure(null);
        }
    }

    /**
     * Request page for from server
     *
     * @param server the credentials of server.
     * @param page the page to fetch.
     * @param responseListener response listener.
     */
    public void requestPage(OHServer server, OHLinkedPage page, final retrofit.Callback<OHLinkedPage> responseListener) {
        OpenHabService service = generateOpenHabService(server, page.getLink());
        service.getPage(new retrofit.Callback<OHLinkedPage>() {
            @Override
            public void success(OHLinkedPage linkedPage, retrofit.client.Response response) {
                Log.d(TAG, "Received page " + response.getUrl());
                responseListener.success(linkedPage, response);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Received page error " + error.getUrl(), error);
                responseListener.failure(error);
            }
        });
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

    /*public void requestItems(final OHServer server, final ItemsRequestListener listener){

        final retrofit.OHCallback<List<ItemDB>> callback = new retrofit.OHCallback<List<ItemDB>>() {
            @Override
            public void success(List<ItemDB> items, retrofit.client.OHResponse response) {
                for(ItemDB item : items){
                    item.setServer(server);
                }
                listener.onSuccess(items);
            }

            @Override
            public void failure(RetrofitError error) {
                listener.onFailure(error.getMessage());
            }
        };

        // Make remote request if not connected to wifi.
        if(!isConnectedWifi(context) || !server.haveLocal()) {
            if (server.haveRemote()){
                OpenHabService service = generateOpenHabService(server, server.getRemoteUrl());
                service.getItems(callback);
            }
            return;
        }

        OpenHabService service = generateOpenHabService(server, server.getLocalUrl());
        service.getItems(new retrofit.OHCallback<List<ItemDB>>() {
            @Override
            public void success(List<ItemDB> items, retrofit.client.OHResponse response) {
                callback.success(items, response);
            }

            @Override
            public void failure(RetrofitError error) {
                if (server.haveRemote()) {
                    OpenHabService service = generateOpenHabService(server, server.getRemoteUrl());
                    service.getItems(callback);
                } else {
                    callback.failure(error);
                    Log.e(TAG, "Request items failed " + error.getUrl(), error);
                }
            }
        });
    }

    public void requestItem(final OHServer server, final String item, final ItemRequestListener listener){

        final retrofit.OHCallback<ItemDB> callback = new retrofit.OHCallback<ItemDB>() {
            @Override
            public void success(ItemDB item, retrofit.client.OHResponse response) {
                listener.onSuccess(item);
            }

            @Override
            public void failure(RetrofitError error) {
                listener.onFailure(error.getMessage());
            }
        };

        // Make remote request if not connected to wifi.
        if(!isConnectedWifi(context) || !server.haveLocal()) {
            if (server.haveRemote()){
                OpenHabService service = generateOpenHabService(server, server.getRemoteUrl());
                service.getItem(item, callback);
            }
            return;
        }

        OpenHabService service = generateOpenHabService(server, server.getLocalUrl());
        Openhab.instance(OHServer.toGeneric(server)).re;
        service.getItem(item, new retrofit.OHCallback<ItemDB>() {
            @Override
            public void success(ItemDB itemHolder, retrofit.client.OHResponse response) {
                callback.success(itemHolder, response);
            }

            @Override
            public void failure(RetrofitError error) {
                if (server.haveRemote()) {
                    OpenHabService service = generateOpenHabService(server, server.getRemoteUrl());
                    service.getItem(item, callback);
                } else {
                    callback.failure(error);
                }
            }
        });
    }*/

    public void requestSitemap(final OHSitemap sitemap, final SitemapRequestListener listener){

        final OHServer server = sitemap.getServer();

        Uri uri = Uri.parse(sitemap.getLink())
                .buildUpon()
                .path(null)
                .build();

        OpenHabService service = generateOpenHabService(server, uri.toString());
        service.getSitemap(sitemap.getName(), new retrofit.Callback<OHSitemap>() {
            @Override
            public void success(OHSitemap sitemap, retrofit.client.Response response) {
                sitemap.setServer(server);
                listener.onSuccess(sitemap);
            }

            @Override
            public void failure(RetrofitError error) {
                listener.onFailure(error.getMessage());
            }
        });
    }

    public void registerMyOpenhabGCM(final OHServer server, String deviceId, String deviceModel, String regId, retrofit.Callback<String> callback){
        MyOpenHabService service = generateMyOpenHabService(server);
        service.registerGCM(deviceId, deviceModel, regId, callback);
    }

    public interface ItemRequestListener{
        void onSuccess(OHItem item);
        void onFailure(String message);
    }

    public interface ItemsRequestListener{
        void onSuccess(List<OHItem> items);
        void onFailure(String message);
    }

    public interface SitemapRequestListener{
        void onSuccess(OHSitemap sitemap);
        void onFailure(String message);
    }

    public interface SitemapsRequestListener{
        void onSuccess(List<OHSitemap> sitemaps);
        void onFailure(String message);
    }

    public static class DummySitemapsRequestListener implements SitemapsRequestListener {
        public void onSuccess(List<OHSitemap> sitemaps){}
        public void onFailure(String message){}
    }
}
