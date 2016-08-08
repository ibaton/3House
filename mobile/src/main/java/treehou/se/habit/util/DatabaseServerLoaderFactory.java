package treehou.se.habit.util;

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
import treehou.se.habit.core.db.model.OHRealm;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.core.db.model.SitemapDB;
import treehou.se.habit.module.ServerLoaderFactory;

public class DatabaseServerLoaderFactory implements ServerLoaderFactory {

    @Inject OHRealm realm;
    @Inject ConnectionFactory connectionFactory;

    @Override
    public OHServer loadServer(Realm realm, long serverId) {
        return ServerDB.load(realm, serverId).toGeneric();
    }

    @Override
    public Observable.Transformer<Realm, OHServer> loadServersRx() {
        return RxUtil.loadServers();
    }


    /**
     * Fetches sitemaps from server.
     * @param context the used to fetch sitemaps.
     * @return
     */
    public Observable.Transformer<OHServer, Pair<OHServer, List<OHSitemap>>> serverToSitemap(Context context) {
        ((HabitApplication)context.getApplicationContext()).component().inject(this);

        return observable -> observable.flatMap(new Func1<OHServer, Observable<List<OHSitemap>>>() {
            @Override
            public Observable<List<OHSitemap>> call(OHServer server) {
                IServerHandler serverHandler = connectionFactory.createServerHandler(server, context);
                return serverHandler.requestSitemapObservable().subscribeOn(Schedulers.io())
                        .onErrorReturn(throwable -> new ArrayList<>());
            }
        }, (server, sitemaps) -> {
            for (OHSitemap sitemap : sitemaps) {
                sitemap.setServer(server);
            }
            return new Pair<>(server, sitemaps);
        }).doOnNext(RxUtil.saveSitemap());
    }

    public Observable.Transformer<Pair<OHServer,List<OHSitemap>>, Pair<OHServer,List<OHSitemap>>> filterDisplaySitemaps() {
        return observable -> observable.map(new Func1<Pair<OHServer, List<OHSitemap>>, Pair<OHServer, List<OHSitemap>>>() {
            @Override
            public Pair<OHServer, List<OHSitemap>> call(Pair<OHServer, List<OHSitemap>> ohServerListPair) {
                List<OHSitemap> sitemaps = new ArrayList<>();
                for(OHSitemap sitemap : ohServerListPair.second){
                    SitemapDB sitemapDB = realm.realm().where(SitemapDB.class)
                            .equalTo("name", sitemap.getName())
                            .equalTo("server.name", ohServerListPair.first.getName())
                            .findFirst();

                    if(sitemapDB == null || sitemapDB.getSettingsDB() == null
                            || sitemapDB.getSettingsDB().isDisplay()){

                        sitemaps.add(sitemap);
                    }
                }
                return new Pair<>(ohServerListPair.first, sitemaps);
            }
        });
    }
}
