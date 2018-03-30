package treehou.se.habit.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.ObservableTransformer;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.dagger.ServerLoaderFactory;
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
        return ServerDB.Companion.load(realm, serverId).toGeneric();
    }

    @Override
    public ObservableTransformer<Realm, OHServer> loadServersRx() {
        return RxUtil.Companion.loadServers();
    }

    @Override
    public ObservableTransformer<ServerSitemapsResponse, ServerSitemapsResponse> filterDisplaySitemaps() {
        return observable -> observable;
    }

    @Override
    public ObservableTransformer<List<OHServer>, List<ServerSitemapsResponse>> serversToSitemap(Context context) {
        return null; // TODO
    }

    @Override
    public ObservableTransformer<List<ServerSitemapsResponse>, List<ServerSitemapsResponse>> filterDisplaySitemapsList() {
        return null; // TODO
    }

    @Override
    public ObservableTransformer<Realm, List<OHServer>> loadAllServersRx() {
        return null; // TODO
    }

    /**
     * Fetches sitemaps from server.
     * @param context the used to fetch sitemaps.
     * @return
     */
    public ObservableTransformer<OHServer, ServerSitemapsResponse> serverToSitemap(Context context) {
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
        .doOnNext(RxUtil.Companion.saveSitemap());
    }
}
