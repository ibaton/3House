package treehou.se.habit.ui.sitemaps.sitemaplist;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.realm.Realm;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import treehou.se.habit.module.RxPresenter;
import treehou.se.habit.module.ServerLoaderFactory;
import treehou.se.habit.util.Settings;

public class SitemapListPresenter extends RxPresenter implements SitemapListContract.Presenter {

    private static final String TAG = SitemapListPresenter.class.getSimpleName();
    private SitemapListContract.View view;
    private Settings settings;
    private ServerLoaderFactory serverLoaderFactory;
    private Realm realm;
    private Context context;
    private String showSitemap = "";
    private Bundle arguments;

    private BehaviorSubject<OHServer> serverBehaviorSubject = BehaviorSubject.create();


    @Inject
    public SitemapListPresenter(@Named("arguments") Bundle arguments, SitemapListContract.View view, Context context, Settings settings, Realm realm, ServerLoaderFactory serverLoaderFactory) {
        this.view = view;
        this.settings = settings;
        this.realm = realm;
        this.serverLoaderFactory = serverLoaderFactory;
        this.context = context;
        this.arguments = arguments;
    }

    @Override
    public void load(Bundle savedData) {
        if(savedData != null) showSitemap = "";
        else showSitemap = arguments.getString(SitemapListFragment.ARG_SHOW_SITEMAP);
    }


    @Override
    public void subscribe() {
        view.clearList();
        loadSitemapsFromServers();
    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void unload() {
    }

    @Override
    public void save(Bundle savedData) {}

    @Override
    public void reloadSitemaps(OHServer server) {
        serverBehaviorSubject.onNext(server);
    }

    /**
     * Load servers from database and request their sitemaps.
     */
    private void loadSitemapsFromServers(){
        Observable.merge(
                realm.asObservable()
                        .compose(serverLoaderFactory.loadServersRx()),
                serverBehaviorSubject.asObservable())
                .doOnNext(server -> view.hideEmptyView())
                .observeOn(Schedulers.io())
                .compose(serverLoaderFactory.serverToSitemap(context))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .compose(serverLoaderFactory.filterDisplaySitemaps())
                .subscribe(
                        serverSitemaps -> populateSitemap(serverSitemaps),
                        throwable -> Log.e(TAG, "Request sitemap failed", throwable)
                );
    }

    private void populateSitemap(Pair<OHServer, List<OHSitemap>> serverSitemaps){
        OHServer server = serverSitemaps.first;
        List<OHSitemap> sitemaps = serverSitemaps.second;

        if(sitemaps.size() <= 0){
            view.showServerError(server);
        } else {
            view.populateSitemaps(serverSitemaps);
            boolean autoloadLast = settings.getAutoloadSitemapRx().get();
            for (OHSitemap sitemap : sitemaps) {
                if (autoloadLast && sitemap.getName().equals(showSitemap)) {
                    showSitemap = null; // Prevents sitemap from being accessed again.
                    openSitemap(server, sitemap);
                }
            }
        }
    }

    @Override
    public void openSitemap(OHServer server, OHSitemap sitemap) {
        settings.setDefaultSitemap(sitemap);
        view.showSitemap(server, sitemap);
    }
}
