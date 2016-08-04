package treehou.se.habit.util;

import android.content.Context;
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.core.db.DBHelper;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.core.db.model.SitemapDB;
import treehou.se.habit.core.db.model.SitemapSettingsDB;

public class RxUtil {

    private RxUtil() {}

    public static <T> Observable.Transformer<T, T> newToMainSchedulers() {
        return observable -> observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Save sitemap to database
     * @return action that saves sitemap.
     */
    public static Action1<Pair<OHServer, List<OHSitemap>>> saveSitemap(){
        return serverDBListPair -> {
            Realm realm = Realm.getDefaultInstance();
            for(OHSitemap sitemap : serverDBListPair.second){

                OHServer server = serverDBListPair.first;
                ServerDB serverDB = realm.where(ServerDB.class)
                        .equalTo("name", server.getName())
                        .equalTo("localurl", server.getLocalUrl())
                        .equalTo("remoteurl", server.getRemoteUrl())
                        .findFirst();

                SitemapDB sitemapDB = realm.where(SitemapDB.class)
                        .equalTo("server.name", serverDBListPair.first.getName())
                        .equalTo("name", sitemap.getName())
                        .findFirst();

                if(sitemapDB == null){
                    SitemapSettingsDB sitemapSettingsDB = new SitemapSettingsDB();
                    sitemapSettingsDB.setDisplay(true);
                    sitemapSettingsDB.setId(DBHelper.getUniqueId(realm, SitemapSettingsDB.class));

                    sitemapDB = new SitemapDB();
                    sitemapDB.setServer(serverDB);
                    sitemapDB.setId(SitemapDB.getUniqueId(realm));
                    sitemapDB.setLabel(sitemap.getLabel());
                    sitemapDB.setLink(sitemap.getLink());
                    sitemapDB.setName(sitemap.getName());

                    realm.beginTransaction();
                    sitemapDB = realm.copyToRealm(sitemapDB);
                    realm.commitTransaction();
                }

                if(sitemapDB.getSettingsDB() == null){
                    SitemapSettingsDB sitemapSettingsDB = new SitemapSettingsDB();
                    boolean showSitemap = !"_default".equalsIgnoreCase(sitemapDB.getName());
                    sitemapSettingsDB.setDisplay(showSitemap);
                    sitemapSettingsDB.setId(DBHelper.getUniqueId(realm, SitemapSettingsDB.class));

                    realm.beginTransaction();
                    sitemapSettingsDB = realm.copyToRealm(sitemapSettingsDB);
                    sitemapDB.setSettingsDB(sitemapSettingsDB);
                    realm.commitTransaction();
                }
            }
            realm.close();
        };
    }

    /**
     * Load servers from database.
     * @return observable for generic server objects.
     */
    public static Observable.Transformer<Realm, OHServer> loadServers() {
        return observable -> observable.flatMap(realmLocal ->
                realmLocal.where(ServerDB.class).isNotEmpty("localurl").or().isNotEmpty("remoteurl").greaterThan("id", 0).findAllAsync().asObservable())
                .flatMap(Observable::from)
                .map(ServerDB::toGeneric)
                .distinct();
    }

    /**
     * Creates a sitemap settings object for sitemap if not already exists
     * @return sitemap with settings set.
     */
    public static Observable.Transformer<SitemapDB, SitemapDB> createSettingsIfEmpty() {
        return observable -> observable.map(new Func1<SitemapDB, SitemapDB>() {
            @Override
            public SitemapDB call(SitemapDB sitemapDB) {
                if (sitemapDB.getSettingsDB() == null) {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    SitemapSettingsDB sitemapSettingsDB = realm.createObject(SitemapSettingsDB.class);
                    sitemapDB.setSettingsDB(sitemapSettingsDB);
                    realm.commitTransaction();
                }
                return sitemapDB;
            }
        });
    }

    public static Observable.Transformer<Pair<OHServer,List<OHSitemap>>, Pair<OHServer,List<OHSitemap>>> filterDisplaySitemaps(Realm realm) {
        return observable -> observable.map(new Func1<Pair<OHServer, List<OHSitemap>>, Pair<OHServer, List<OHSitemap>>>() {
            @Override
            public Pair<OHServer, List<OHSitemap>> call(Pair<OHServer, List<OHSitemap>> ohServerListPair) {
                List<OHSitemap> sitemaps = new ArrayList<>();
                for(OHSitemap sitemap : ohServerListPair.second){
                    SitemapDB sitemapDB = realm.where(SitemapDB.class)
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
