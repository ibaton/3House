package treehou.se.habit.util;

import android.content.Context;
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.core.db.model.ServerDB;

public class RxUtil {

    private RxUtil() {}

    public static <T> Observable.Transformer<T, T> newToMainSchedulers() {
        return observable -> observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable.Transformer<OHServer, Pair<OHServer, List<OHSitemap>>> serverToSitemap(Context context) {
        return observable -> observable.flatMap((Func1<OHServer, Observable<List<OHSitemap>>>) new Func1<OHServer, Observable<List<OHSitemap>>>() {
            @Override
            public Observable<List<OHSitemap>> call(OHServer server) {
                IServerHandler serverHandler = new Connector.ServerHandler(server, context);
                return serverHandler.requestSitemapObservable().subscribeOn(Schedulers.io())
                        .onErrorReturn(throwable -> new ArrayList<>());
            }
        }, (server, sitemaps) -> {
            for (OHSitemap sitemap : sitemaps) {
                sitemap.setServer(server);
            }
            return new Pair<>(server, sitemaps);
        });
    }

    public static Observable.Transformer<Realm, OHServer> loadServers() {
        return observable -> observable.flatMap(realmLocal ->
                realmLocal.where(ServerDB.class).isNotEmpty("localurl").or().isNotEmpty("remoteurl").greaterThan("id", 0).findAllAsync().asObservable())
                .flatMap(Observable::from)
                .map(ServerDB::toGeneric);
    }
}
