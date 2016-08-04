package treehou.se.habit.util;

import android.content.Context;
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.IServerHandler;

public class RxConnectorUtil {

    @Inject ConnectionFactory connectionFactory;

    public RxConnectorUtil() {}

    /**
     * Fetches sitemaps from server.
     * @param context the used to fetch sitemaps.
     * @return
     */
    public Observable.Transformer<OHServer, Pair<OHServer, List<OHSitemap>>> serverToSitemap(Context context) {
        return observable -> observable.flatMap(new Func1<OHServer, Observable<List<OHSitemap>>>() {
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
        })
        .doOnNext(RxUtil.saveSitemap());
    }
}
