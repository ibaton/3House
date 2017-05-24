package treehou.se.habit.ui.sitemaps.sitemaplist;

import android.content.Context;
import android.os.Bundle;
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
        super.load(savedData);
        if (savedData != null) showSitemap = "";
        else showSitemap = arguments.getString(SitemapListFragment.ARG_SHOW_SITEMAP);
    }


    @Override
    public void subscribe() {
        super.subscribe();
        view.clearList();
        loadSitemapsFromServers();
    }

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
                        this::populateSitemap,
                        throwable -> Log.e(TAG, "Request sitemap failed", throwable)
                );
    }

    private void populateSitemap(ServerLoaderFactory.ServerSitemapsResponse serverSitemaps){
        OHServer server = serverSitemaps.getServer();
        List<OHSitemap> sitemaps = serverSitemaps.getSitemaps();

        if(serverSitemaps.hasError()){
            view.showServerError(server, serverSitemaps.getError());
        } else {
            view.populateSitemaps(server, sitemaps);
            boolean autoloadLast = settings.getAutoloadSitemapRx().toBlocking().first();
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
