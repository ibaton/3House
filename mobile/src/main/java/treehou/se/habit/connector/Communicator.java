package treehou.se.habit.connector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Cache;
import com.squareup.picasso.Callback;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.X509TrustManager;

import de.duenndns.ssl.MemorizingTrustManager;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import se.treehou.ng.ohcommunicator.util.ConnectorUtil;
import treehou.se.habit.core.db.settings.WidgetSettingsDB;
import treehou.se.habit.util.Util;

public class Communicator {

    private static final String TAG = "Communicator";

    private static final String MY_OPENHAB_URL = "https://my.openhab.org";

    private static Communicator mInstance;
    private Context context;
    private Map<OHServer, Picasso> requestLoaders = new HashMap<>();
    private X509TrustManager trustManager;

    public static synchronized Communicator instance(Context context){
        if (mInstance == null) {
            mInstance = new Communicator(context);
        }
        return mInstance;
    }

    private Communicator(Context context){
        this.context = context;
        trustManager = new MemorizingTrustManager(context);
    }

    private int scrubNumberValue(int number, final int min, final int max){
        return Math.max(Math.min(number, max), min);
    }

    public void incDec(final OHServer server, final String itemName, final int value, final int min, final int max){

        final IServerHandler serverHandler = new Connector.ServerHandler(server, context, null, null, null);
        serverHandler.requestItemRx(itemName)
                .subscribeOn(Schedulers.io())
                .doOnNext(newItem -> Log.d(TAG, "Item state " + newItem.getState() + " " + newItem.getType()))
                .subscribe(newItem -> {
                    String state = newItem.getState();
                    if (treehou.se.habit.util.Constants.INSTANCE.getSUPPORT_INC_DEC().contains(newItem.getType())) {
                        if (Constants.INSTANCE.getCOMMAND_OFF().equals(state) || Constants.INSTANCE.getCOMMAND_UNINITIALIZED().equals(state)) {
                            if (value > 0) {
                                serverHandler.sendCommand(newItem.getName(), String.valueOf(scrubNumberValue(min + value, min, max)));
                            }
                        } else if (Constants.INSTANCE.getCOMMAND_ON().equals(state)) {
                            if (value < 0) {
                                serverHandler.sendCommand(newItem.getName(), String.valueOf(scrubNumberValue(max + value, min, max)));
                            }
                        } else {
                            try {
                                int itemVal = scrubNumberValue(Integer.parseInt(newItem.getState()) + value, min, max);
                                Log.e(TAG, "Sending sendCommand " + itemVal + " value " + value);
                                serverHandler.sendCommand(newItem.getName(), String.valueOf(itemVal));
                            } catch (NumberFormatException e) {
                                Log.e(TAG, "Could not parse state " + newItem.getState(), e);
                            }
                        }
                    }
                }, throwable -> Log.d(TAG, "incDec onError"));
    }

    private Picasso buildPicasso(Context context, final OHServer server){

        if(requestLoaders.containsKey(server)){
            return requestLoaders.get(server);
        }

        OkHttpClient.Builder httpClient = TrustModifier.createAcceptAllClient();
        httpClient.interceptors().add(chain -> {
            Request.Builder newRequest = chain.request().newBuilder();
            if (server.requiresAuth()) {
                newRequest.header(Constants.INSTANCE.getHEADER_AUTHENTICATION(), ConnectorUtil.createAuthValue(server.getUsername(), server.getPassword()));
            }

            return chain.proceed(newRequest.build());
        });

        final Picasso picasso = new Picasso.Builder(context)
                .loggingEnabled(true)
                .downloader(new OkHttp3Downloader(httpClient.build()))
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
     * @param viewGoneOnFail true to set view to gone if fail.
     */
    public void loadImage(final OHServer server, final Uri imageUrl, final ImageView imageView, boolean viewGoneOnFail){

        Log.d(TAG, "onBitmapLoaded image start " + imageUrl.toString());
        final Callback callback = new Callback() {
            @Override
            public void onSuccess() {
                imageView.setVisibility(View.VISIBLE);
                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

                Realm realm = Realm.getDefaultInstance();
                WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(realm);

                int imageBackground = Util.INSTANCE.getBackground(context, bitmap, settings.getImageBackground());
                realm.close();

                imageView.setBackgroundColor(imageBackground);
            }

            @Override
            public void onError() {
                imageView.setVisibility(viewGoneOnFail ? View.GONE : View.INVISIBLE);
                Log.d(TAG, "onBitmapLoaded image load failed " + imageUrl);
            }
        };

        Picasso picasso = buildPicasso(context, server);
        picasso.load(imageUrl.toString())
            .noFade()
            .placeholder(imageView.getDrawable())
            .into(imageView, callback); // problem when saving null image
    }
}
