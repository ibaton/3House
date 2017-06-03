package treehou.se.habit.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import rx.Observable;
import rx.schedulers.Schedulers;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
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
    public Observable.Transformer<ServerSitemapsResponse, ServerSitemapsResponse> filterDisplaySitemaps() {
        return observable -> observable;
    }

    @Override
    public Observable.Transformer<List<OHServer>, List<ServerSitemapsResponse>> serversToSitemap(Context context) {
        return null; // TODO
    }

    @Override
    public Observable.Transformer<List<ServerSitemapsResponse>, List<ServerSitemapsResponse>> filterDisplaySitemapsList() {
        return null; // TODO
    }

    @Override
    public Observable.Transformer<Realm, List<OHServer>> loadAllServersRx() {
        return null; // TODO
    }

    /**
     * Fetches sitemaps from server.
     * @param context the used to fetch sitemaps.
     * @return
     */
    public Observable.Transformer<OHServer, ServerSitemapsResponse> serverToSitemap(Context context) {
        return observable -> observable.flatMap(server -> {
            IServerHandler serverHandler = connectionFactory.createServerHandler(server, context);
            return serverHandler.requestSitemapRx()
                    .map(ohSitemaps -> new ServerSitemapsResponse(server, ohSitemaps))
                    .subscribeOn(Schedulers.io())
                    .onErrorReturn(error -> new ServerSitemapsResponse(server, new ArrayList<>(), error));
        }, (server, response) -> {
            for (OHSitemap sitemap : response.getSitemaps()) {
                sitemap.setServer(server);
            }
            return response;
        })
        .doOnNext(RxUtil.saveSitemap());
    }
}
