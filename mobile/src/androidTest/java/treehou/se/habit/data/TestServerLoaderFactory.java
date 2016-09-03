package treehou.se.habit.data;

import android.content.Context;
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.module.ServerLoaderFactory;
import treehou.se.habit.util.ConnectionFactory;
import treehou.se.habit.util.RxUtil;

public class TestServerLoaderFactory implements ServerLoaderFactory {

    private ConnectionFactory connectionFactory;

    @Inject
    public TestServerLoaderFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public OHServer loadServer(Realm realm, long serverId) {
        return ServerDB.load(realm, serverId).toGeneric();
    }

    @Override
    public Observable.Transformer<Realm, OHServer> loadServersRx() {
        return RxUtil.loadServers();
    }

    @Override
    public Observable.Transformer<Pair<OHServer, List<OHSitemap>>, Pair<OHServer, List<OHSitemap>>> filterDisplaySitemaps() {
        return observable -> observable;
    }

    /**
     * Fetches sitemaps from server.
     * @param context the used to fetch sitemaps.
     * @return
     */
    public Observable.Transformer<OHServer, Pair<OHServer, List<OHSitemap>>> serverToSitemap(Context context) {
        return observable -> observable.flatMap(new Func1<OHServer, Observable<List<OHSitemap>>>() {
            @Override
            public Observable<List<OHSitemap>> call(OHServer server) {
                IServerHandler serverHandler = connectionFactory.createServerHandler(server, context);
                return serverHandler.requestSitemapRx().subscribeOn(Schedulers.io())
                        .onErrorReturn(throwable -> new ArrayList<>());
            }
        }, (server, sitemaps) -> {
            for (OHSitemap sitemap : sitemaps) {
                sitemap.setServer(server);
            }
            return new Pair<>(server, sitemaps);
        })
                .doOnNext(RxUtil.saveSitemap());
    }
}
