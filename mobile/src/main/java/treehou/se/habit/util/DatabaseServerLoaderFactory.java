package treehou.se.habit.util;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.core.db.OHRealm;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.core.db.model.SitemapDB;
import treehou.se.habit.module.ServerLoaderFactory;

public class DatabaseServerLoaderFactory implements ServerLoaderFactory {

    private static final String TAG = DatabaseServerLoaderFactory.class.getSimpleName();

    private OHRealm realm;
    private ConnectionFactory connectionFactory;

    @Inject
    public DatabaseServerLoaderFactory(OHRealm realm, ConnectionFactory connectionFactory) {
        this.realm = realm;
        this.connectionFactory = connectionFactory;
    }

    @Override
    public OHServer loadServer(Realm realm, long serverId) {
        return ServerDB.Companion.load(realm, serverId).toGeneric();
    }

    @Override
    public ObservableTransformer<Realm, OHServer> loadServersRx() {
        return RxUtil.loadServers();
    }

    @Override
    public ObservableTransformer<Realm, List<OHServer>> loadAllServersRx() {
        return observable -> observable.flatMap(realmLocal ->
                realmLocal.where(ServerDB.class).isNotEmpty("localurl").or().isNotEmpty("remoteurl").greaterThan("id", 0).findAllAsync().asFlowable().toObservable())
                .map(serverDBS -> {
                    List<OHServer> serverList = new ArrayList<>();
                    for(ServerDB serverDB : serverDBS){
                        serverList.add(serverDB.toGeneric());
                    }
                    return serverList;
                })
                .distinct();
    }

    @Override
    public ObservableTransformer<List<OHServer>, List<ServerSitemapsResponse>> serversToSitemap(Context context) {
        return observable -> observable
                .switchMap(servers -> {
                    List<Observable<ServerSitemapsResponse>> sitemapResponseRx = new ArrayList<>();
                    for (OHServer server : servers) {
                        Observable<ServerSitemapsResponse> serverSitemapsResponseRx = Observable.just(server)
                                .compose(serverToSitemap(context))
                                .subscribeOn(Schedulers.io())
                                .startWith(Companion.getEMPTY_RESPONSE());

                        sitemapResponseRx.add(serverSitemapsResponseRx);
                    }

                    return Observable.<ServerSitemapsResponse, List<ServerSitemapsResponse>>combineLatest(sitemapResponseRx, responsesObject -> {
                        List<ServerSitemapsResponse> responses = new ArrayList<>();
                        for (Object responseObject : responsesObject) {
                            ServerSitemapsResponse response = (ServerSitemapsResponse) responseObject;
                            responses.add(response);
                        }

                        return responses;
                    });
                });
    }

    /**
     * Fetches sitemaps from server.
     * @param context the used to fetch sitemaps.
     * @return
     */
    public ObservableTransformer<OHServer, ServerSitemapsResponse> serverToSitemap(Context context) {
        return observable -> observable
                .flatMap(server -> {
            IServerHandler serverHandler = connectionFactory.createServerHandler(server, context);
            return serverHandler.requestSitemapRx()
                    .map(SitemapResponse::new)
                    .subscribeOn(Schedulers.io())
                    .doOnError(e -> Log.e(TAG, "Failed to load sitemap", e))
                    .onErrorReturn(throwable -> new SitemapResponse(new ArrayList<>(), throwable));
        }, (server, sitemapResponse) -> {
            for (OHSitemap sitemap : sitemapResponse.sitemaps) {
                sitemap.setServer(server);
            }
            return new ServerSitemapsResponse(server, sitemapResponse.sitemaps, sitemapResponse.error);
        })
        .doOnNext(RxUtil.saveSitemap());
    }

    public ObservableTransformer<ServerSitemapsResponse, ServerSitemapsResponse> filterDisplaySitemaps() {
        return observable -> observable.map((Function<ServerSitemapsResponse, ServerSitemapsResponse>) this::filterDisplaySitemaps);
    }

    @Override
    public ObservableTransformer<List<ServerSitemapsResponse>, List<ServerSitemapsResponse>> filterDisplaySitemapsList() {
        return observable -> observable.map((Function<List<ServerSitemapsResponse>, List<ServerSitemapsResponse>>) serverSitemapsResponses -> {
            List<ServerSitemapsResponse> responses = new ArrayList<>();
            for (ServerSitemapsResponse sitemapsResponse : serverSitemapsResponses){
                responses.add(filterDisplaySitemaps(sitemapsResponse));
            }
            return responses;
        });
    }

    private ServerSitemapsResponse filterDisplaySitemaps(ServerSitemapsResponse response){
        List<OHSitemap> sitemaps = new ArrayList<>();
        for(OHSitemap sitemap : response.getSitemaps()){
            SitemapDB sitemapDB = realm.realm().where(SitemapDB.class)
                    .equalTo("name", sitemap.getName())
                    .equalTo("server.name", response.getServer().getName())
                    .findFirst();

            if(sitemapDB == null || sitemapDB.getSettingsDB() == null
                    || sitemapDB.getSettingsDB().getDisplay()){

                sitemaps.add(sitemap);
            }
        }
        return new ServerSitemapsResponse(response.getServer(), sitemaps, response.getError());
    }

    private static class SitemapResponse{
        public Throwable error;
        public List<OHSitemap> sitemaps;

        public SitemapResponse(List<OHSitemap> sitemaps) {
            this.sitemaps = sitemaps;
        }

        public SitemapResponse(List<OHSitemap> sitemaps, Throwable error) {
            this.error = error;
            this.sitemaps = sitemaps;
        }
    }
}
