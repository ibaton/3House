package treehou.se.habit.gcm;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import treehou.se.habit.module.ServerLoaderFactory;
import treehou.se.habit.util.ConnectionFactory;
import treehou.se.habit.util.Constants;

public class GoogleCloudMessageConnector {
    private static final String TAG = GoogleCloudMessageConnector.class.getSimpleName();

    @Inject
    protected ServerLoaderFactory serverLoaderFactory;
    @Inject
    protected Realm realm;
    @Inject
    protected ConnectionFactory connectionFactory;

    @Inject
    public GoogleCloudMessageConnector() {
    }

    public void registerGcm(Context context) {
        Observable.combineLatest(
                Observable.fromCallable(() -> {
                    try {
                        return AdvertisingIdClient.getAdvertisingIdInfo(context).getId();
                    } catch (IOException | GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                        Log.e(TAG, "Error getting advertising ID: " + e.getMessage(), e);
                        return "";
                    }
                }).subscribeOn(Schedulers.io()),
                realm.asFlowable().toObservable().compose(serverLoaderFactory.loadServersRx()),
                Pair::create)
                .filter(deviceIdOHServerPair -> !deviceIdOHServerPair.first.isEmpty())
                .flatMap((Function<Pair<String, OHServer>, ObservableSource<?>>) deviceIdOHServerPair -> {

                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                    String deviceId = deviceIdOHServerPair.first;
                    OHServer ohServer = deviceIdOHServerPair.second;
                    String registrationId;
                    try {
                        registrationId = gcm.register(Constants.GCM_SENDER_ID);
                    } catch (IOException e) {
                        Log.e(TAG, "Error getting GCM ID: " + e.getMessage(), e);
                        return Observable.empty();
                    }

                    String deviceModel;
                    try {
                        deviceModel = URLEncoder.encode(Build.MODEL, "UTF-8");
                    } catch (UnsupportedEncodingException ex) {
                        Log.d(TAG, "Could not encode device model: " + ex.getMessage());
                        return Observable.empty();
                    }

                    return connectionFactory.createServerHandler(ohServer, context).registerGcm(deviceId, deviceModel, registrationId);
                })
                .subscribe();
    }
}
