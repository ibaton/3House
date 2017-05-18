package treehou.se.habit.ui.sitemaps.sitemap;


import android.content.Context;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.module.RxPresenter;
import treehou.se.habit.util.ConnectionFactory;
import treehou.se.habit.util.logging.Logger;

public class SitemapPresenter extends RxPresenter implements SitemapContract.Presenter {

    private static final String TAG = SitemapPresenter.class.getSimpleName();

    private SitemapContract.View view;
    private Logger log;
    private ConnectionFactory connectionFactory;
    private Context context;
    private OHSitemap sitemap;
    private ServerDB server;

    @Inject
    public SitemapPresenter(SitemapContract.View view, ServerDB server, OHSitemap sitemap, Context context, Logger log, ConnectionFactory connectionFactory) {
        this.view = view;
        this.log = log;
        this.context = context;
        this.connectionFactory = connectionFactory;
        this.server = server;
        this.sitemap = sitemap;
    }

    @Override
    public void showPage(OHLinkedPage page) {
        Log.d(TAG, "Received page " + page);
        view.showPage(server, page);
    }

    @Override
    public void subscribe() {
        super.subscribe();

        if(sitemap == null){
            view.removeAllPages();
            return;
        }

        if(!view.hasPage()) {
            final IServerHandler serverHandler = connectionFactory.createServerHandler(sitemap.getServer(), context);
            serverHandler.requestPageRx(sitemap.getHomepage())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::showPage, e -> log.w(TAG, "Received page failed", e));
        }

        EventBus.getDefault().register(this);
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
        EventBus.getDefault().unregister(this);
    }

    /**
     * User requested to move to new page.
     *
     * @param event
     */
    @Subscribe
    public void onEvent(OHLinkedPage event){
        showPage(event);
    }
}
