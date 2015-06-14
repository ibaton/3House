package treehou.se.habit.connector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;
import treehou.se.habit.connector.requests.GsonRequest;
import treehou.se.habit.core.Item;
import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Server;
import treehou.se.habit.core.Sitemap;
import treehou.se.habit.core.settings.WidgetSettings;
import treehou.se.habit.util.Util;

/**
 * Created by ibaton on 2014-09-10.
 */
public class Communicator {

    private static final String TAG = "Communicator";

    private static Communicator mInstance;
    private Context context;
    private RequestQueue requestQueue;
    private Map<Server, Picasso> requestLoaders = new HashMap<>();

    public static synchronized Communicator instance(Context context){
        if (mInstance == null) {
            mInstance = new Communicator(context);
        }
        return mInstance;
    }

    private Communicator(Context context){
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public static OpenHabService generateOpenHabService(Server server, boolean local){
        return generateOpenHabService(server, local ? server.getLocalUrl() : server.getRemoteUrl());
    }

    public static OpenHabService generateOpenHabService(Server server, String url){
        return ServiceGenerator.createService(OpenHabService.class, url, server.getUsername(), server.getPassword());
    }

    public static NetworkInfo getNetworkInfo(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public static boolean isConnectedWifi(Context context){
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public void command(Server server, Item item, final String command){
        command(server, item.getName(), command);
    }

    public void command(final Server server, final String item, final String command){

        final retrofit.Callback<retrofit.client.Response> callback = new retrofit.Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response body, retrofit.client.Response response) {
                Log.d(TAG, "Sent command " + command);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Error: " + error.getCause() + " " + error.getUrl());
                Log.e(TAG, "Error: " + error);
            }
        };

        // Make remote request if not connected to wifi.
        if(!isConnectedWifi(context) || !server.haveLocal()) {
            if (server.haveRemote()){
                OpenHabService service = generateOpenHabService(server, server.getRemoteUrl());
                service.sendCommand(command, item, callback);
            }
            return;
        }

        final OpenHabService service = generateOpenHabService(server, server.getLocalUrl());
        service.sendCommand(command, item, new retrofit.Callback<retrofit.client.Response>() {

            @Override
            public void success(retrofit.client.Response body, retrofit.client.Response response) {
                Log.d(TAG, "Sent command " + command);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Error: " + error.getCause() + " " + error.getUrl());
                Log.e(TAG, "Error: " + error);
                if (server.haveRemote()) {
                    OpenHabService service = generateOpenHabService(server, server.getRemoteUrl());
                    service.sendCommand(command, item, callback);
                } else {
                    callback.failure(error);
                }
            }
        });
    }

    private int scrubNumberValue(int number, final int min, final int max){
        return Math.max(Math.min(number, max), min);
    }

    public void incDec(final Server server, final Item item, final int value, final int min, final int max){
        requestItem(server, item.getName(), new ItemRequestListener() {
            @Override
            public void onSuccess(Item newItem) {
                Log.d(TAG, "Item state " + newItem.getState() + " " + newItem.getType());
                String state = newItem.getState();
                if (treehou.se.habit.Constants.SUPPORT_INC_DEC.contains(newItem.getType())) {
                    if (Constants.COMMAND_OFF.equals(state) || Constants.COMMAND_UNINITIALIZED.equals(state)) {
                        if (value > 0) {
                            command(server, newItem, String.valueOf(scrubNumberValue(min + value, min, max)));
                        }
                    } else if (Constants.COMMAND_ON.equals(state)) {
                        if (value < 0) {
                            command(server, newItem, String.valueOf(scrubNumberValue(max + value, min, max)));
                        }
                    } else {
                        try {
                            int itemVal = scrubNumberValue(Integer.parseInt(newItem.getState()) + value, min, max);
                            Log.e(TAG, "Sending command " + itemVal + " value " + value);
                            command(server, newItem, String.valueOf(itemVal));
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Could not parse state " + newItem.getState(), e);
                            return;
                        }
                    }
                }
            }

            @Override
            public void onFailure(String message) {
                Log.e(TAG, "incDec " + message);
            }
        });
    }

    public Picasso buildPicasso(Context context, final Server server){

        if(requestLoaders.containsKey(server)){
            return requestLoaders.get(server);
        }

        OkHttpClient httpClient = TrustModifier.createAcceptAllClient();
        httpClient.interceptors().add(new Interceptor() {
            @Override
            public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {

                com.squareup.okhttp.Request.Builder newRequest = chain.request().newBuilder();
                if(!TextUtils.isEmpty(server.getUsername()) && !TextUtils.isEmpty(server.getPassword())) {
                    newRequest.addHeader("Authorization", ConnectorUtil.createAuthValue(server.getUsername(), server.getPassword()));
                }

                return chain.proceed(newRequest.build());
            }
        });

        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new OkHttpDownloader(httpClient));

        //, TrustModifier.createAcceptAllClient()

        /*builder.downloader(new OkHttpDownloader(TrustModifier.createAcceptAllClient()) {
            @Override
            protected HttpURLConnection openConnection(Uri uri) throws IOException {
                Log.d(TAG, "onBitmapLoaded openConnection " + uri);
                HttpURLConnection connection = super.openConnection(uri);

                if(!TextUtils.isEmpty(server.getUsername()) && !TextUtils.isEmpty(server.getPassword())) {
                    connection.setRequestProperty("Authorization", ConnectorUtil.createAuthValue(server.getUsername(), server.getPassword()));
                }

                return connection;
            }

            @Override
            public Response load(Uri uri, int networkPolicy) throws IOException {
                return super.load(uri, networkPolicy);
            }
        });*/
        builder.memoryCache(new LruCache(context));
        final Picasso picasso = builder.build();

        requestLoaders.put(server,picasso);

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
    public void loadImage(final Server server, final URL imageUrl, final ImageView imageView, boolean useCache){
        Log.d(TAG, "onBitmapLoaded image start " + imageUrl.toString());
        final Callback callback = new Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onBitmapLoaded image load success");
                imageView.setVisibility(View.VISIBLE);
                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

                WidgetSettings settings = WidgetSettings.loadGlobal(context);

                int imageBackground = Util.getBackground(context, bitmap, settings.getImageBackground());
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
    public void loadImage(final Server server, final URL imageUrl, final ImageView imageView){
        loadImage(server, imageUrl, imageView, true);
    }

    public void cancelRequest(String tag){
        requestQueue.cancelAll(tag);
    }

    public <T> void addRequest(final Server server, final GsonRequest<T> request){
        addRequest(server, request, true);
    }

    /**
     *
     * @param server
     * @param request
     * @param multiServerRetry try to connect to local and remote server.
     * @param <T>
     */
    public <T> void addRequest(final Server server, final GsonRequest<T> request, boolean multiServerRetry){

        final Uri localUrl = ConnectorUtil.changeHostUrl(Uri.parse(request.getUrl()),Uri.parse(server.getLocalUrl()));
        final Uri remoteRequestUrl = ConnectorUtil.changeHostUrl(Uri.parse(request.getUrl()), Uri.parse(server.getRemoteUrl()));

        if(!multiServerRetry){
            requestQueue.add(request);
            return;
        }

        final Response.ErrorListener errorListener = request.getErrorListener();
        final Response.Listener<T> listener = request.getResponseListener();

        // Make remote request if not connected to wifi.
        if(!isConnectedWifi(context) || !server.haveLocal()) {
            if (server.haveRemote()){
                GsonRequest<T> remoteRequest = new GsonRequest<>(
                        request.getMethod(), remoteRequestUrl.toString(),
                        server.getUsername(), server.getPassword(),
                        request.getClazz(), listener, errorListener);
                requestQueue.add(remoteRequest);
            }
            return;
        }

        // Fallback to remote on failure.
        Response.ErrorListener retryErrorResponse = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (server.haveRemote()) {
                    Log.d(TAG, "addRequest local error " + error);
                    GsonRequest<T> remoteRequest = new GsonRequest<>(
                            request.getMethod(), remoteRequestUrl.toString(),
                            server.getUsername(), server.getPassword(),
                            request.getClazz(), listener, errorListener);
                    Log.d(TAG, "addRequest remote " + remoteRequest.getUrl());
                    requestQueue.add(remoteRequest);
                }
            }
        };

        // Create remote request. Fallback to remote if have any
        GsonRequest<T> newRequest = new GsonRequest<>(
                request.getMethod(), localUrl.toString(),
                server.getUsername(), server.getPassword(),
                request.getClazz(), listener, server.haveRemote()?retryErrorResponse:errorListener);
        requestQueue.add(newRequest);
        Log.d(TAG, "addRequest local " + request.getUrl());
    }

    public void addBasicRequest(final Request request){
        requestQueue.add(request);
    }

    public void requestSitemaps(String tag, final Server server, final SitemapsRequestListener listener){

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

    public void requestPage(String tag, Server server, String link, Response.Listener<LinkedPage> successListener, Response.ErrorListener errorListener) {
        Log.d(TAG, "Requesting page " + link);
        GsonRequest<LinkedPage> request = new GsonRequest<>(
                Request.Method.GET, link,
                server.getUsername(), server.getPassword(),
                LinkedPage.class,
                successListener, errorListener);
        request.setTag(tag);
        addRequest(server, request, false);
    }

    public class MultiSitemapRequest implements retrofit.Callback<Sitemap.SitemapHolder> {

        private int runningRequests;
        private SitemapsRequestListener listener;
        private Server server;

        public MultiSitemapRequest(int requests, final SitemapsRequestListener listener, Server server) {
            this.runningRequests = requests;
            this.listener = listener;
            this.server = server;
        }

        @Override
        public void success(Sitemap.SitemapHolder sitemapHolder, retrofit.client.Response response) {
            List<Sitemap> sitemaps = sitemapHolder.sitemap;
            for(Sitemap sitemap : sitemaps) {
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
                Log.w(TAG, "Failed to connect to server " + error.getMessage() + " " + server.getUrl());
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

    public void requestItems(final Server server, final ItemsRequestListener listener){

        final retrofit.Callback<Item.ItemHolder> callback = new retrofit.Callback<Item.ItemHolder>() {
            @Override
            public void success(Item.ItemHolder itemHolder, retrofit.client.Response response) {
                List<Item> items = itemHolder.item;
                for(Item item : items){
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
        service.getItems(new retrofit.Callback<Item.ItemHolder>() {
            @Override
            public void success(Item.ItemHolder itemHolder, retrofit.client.Response response) {
                callback.success(itemHolder, response);
            }

            @Override
            public void failure(RetrofitError error) {
                if(server.haveRemote()){
                    OpenHabService service = generateOpenHabService(server, server.getRemoteUrl());
                    service.getItems(callback);
                }else{
                    callback.failure(error);
                }
            }
        });
    }

    public void requestItem(final Server server, final String item, final ItemRequestListener listener){

        final retrofit.Callback<Item> callback = new retrofit.Callback<Item>() {
            @Override
            public void success(Item item, retrofit.client.Response response) {
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
        service.getItem(item, new retrofit.Callback<Item>() {
            @Override
            public void success(Item itemHolder, retrofit.client.Response response) {
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
    }

    public void requestSitemap(final Sitemap sitemap, final SitemapRequestListener listener){

        final Server server = sitemap.getServer();

        Uri uri = Uri.parse(sitemap.getLink())
                .buildUpon()
                .path(null)
                .build();

        OpenHabService service = generateOpenHabService(server, uri.toString());
        service.getSitemap(sitemap.getName(), new retrofit.Callback<Sitemap>() {
            @Override
            public void success(Sitemap sitemap, retrofit.client.Response response) {
                sitemap.setServer(server);
                listener.onSuccess(sitemap);
            }

            @Override
            public void failure(RetrofitError error) {
                listener.onFailure(error.getMessage());
            }
        });
    }

    public interface ItemRequestListener{
        public void onSuccess(Item item);
        public void onFailure(String message);
    }

    public interface ItemsRequestListener{
        public void onSuccess(List<Item> items);
        public void onFailure(String message);
    }

    public interface SitemapRequestListener{
        public void onSuccess(Sitemap sitemap);
        public void onFailure(String message);
    }

    public interface SitemapsRequestListener{
        public void onSuccess(List<Sitemap> sitemaps);
        public void onFailure(String message);
    }
}
