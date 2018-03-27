package treehou.se.habit.util;


import android.text.TextUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import treehou.se.habit.core.db.DBHelper;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.core.db.model.SitemapDB;
import treehou.se.habit.core.db.model.SitemapSettingsDB;
import treehou.se.habit.dagger.ServerLoaderFactory;

public class RxUtil {

    public static <T> ObservableTransformer<T, T> newToMainSchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Save sitemap to database
     * @return action that saves sitemap.
     */
    public static Consumer<ServerLoaderFactory.ServerSitemapsResponse> saveSitemap(){
        return sitemapResponse -> {
            Realm realm = Realm.getDefaultInstance();
            for(OHSitemap sitemap : sitemapResponse.getSitemaps()){

                OHServer server = sitemapResponse.getServer();
                ServerDB serverDB = realm.where(ServerDB.class)
                        .equalTo("name", server.getName())
                        .equalTo("localurl", server.getLocalUrl())
                        .equalTo("remoteurl", server.getRemoteUrl())
                        .findFirst();

                SitemapDB sitemapDB = realm.where(SitemapDB.class)
                        .equalTo("server.name", sitemapResponse.getServer().getName())
                        .equalTo("name", sitemap.getName())
                        .findFirst();

                if(sitemapDB == null){
                    SitemapSettingsDB sitemapSettingsDB = new SitemapSettingsDB();
                    sitemapSettingsDB.setDisplay(true);
                    sitemapSettingsDB.setId(DBHelper.getUniqueId(realm, SitemapSettingsDB.class));

                    sitemapDB = new SitemapDB();
                    sitemapDB.setServer(serverDB);
                    sitemapDB.setId(SitemapDB.Companion.getUniqueId(realm));
                    sitemapDB.setLabel(sitemap.getLabel());
                    sitemapDB.setLink(sitemap.getLink());
                    sitemapDB.setName(sitemap.getName());

                    realm.beginTransaction();
                    sitemapDB = realm.copyToRealmOrUpdate(sitemapDB);
                    realm.commitTransaction();
                }

                if(sitemapDB.getSettingsDB() == null){
                    SitemapSettingsDB sitemapSettingsDB = new SitemapSettingsDB();
                    boolean showSitemap = !"_default".equalsIgnoreCase(sitemapDB.getName());
                    sitemapSettingsDB.setDisplay(showSitemap);
                    sitemapSettingsDB.setId(DBHelper.getUniqueId(realm, SitemapSettingsDB.class));

                    realm.beginTransaction();
                    sitemapSettingsDB = realm.copyToRealmOrUpdate(sitemapSettingsDB);
                    sitemapDB.setSettingsDB(sitemapSettingsDB);
                    realm.commitTransaction();
                }
            }
            realm.close();
        };
    }

    /**
     * Filter myopenhab servers
     * @return remove all non myopenhabservers from stream
     */
    public ObservableTransformer<OHServer, OHServer> filterMyOpenhabServers() {
        return observable -> observable.filter((Predicate<OHServer>) ohServer -> {
            String remoteUrl = ohServer.getRemoteUrl();
            String localUrl = ohServer.getRemoteUrl();
            remoteUrl = TextUtils.isEmpty(remoteUrl) ? "" : remoteUrl;
            localUrl = TextUtils.isEmpty(localUrl) ? "" : localUrl;

            return localUrl.contains(Constants.INSTANCE.getMY_OPENHAB_URL_COMPARATOR()) || remoteUrl.contains(Constants.INSTANCE.getMY_OPENHAB_URL_COMPARATOR());
        });
    }

    /**
     * Load servers from database.
     * @return observable for generic server objects.
     */
    public static ObservableTransformer<Realm, OHServer> loadServers() {
        return observable -> observable.flatMap(realmLocal ->
                realmLocal.where(ServerDB.class).isNotEmpty("localurl").or().isNotEmpty("remoteurl").greaterThan("id", 0).findAllAsync().asFlowable().toObservable())
                .flatMap(Observable::fromIterable)
                .map(ServerDB::toGeneric)
                .distinct();
    }
}
