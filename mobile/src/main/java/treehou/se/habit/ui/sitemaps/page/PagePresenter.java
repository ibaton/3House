package treehou.se.habit.ui.sitemaps.page;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.realm.Realm;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import se.treehou.ng.ohcommunicator.util.GsonHelper;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.module.RxPresenter;
import treehou.se.habit.module.ServerLoaderFactory;
import treehou.se.habit.ui.widgets.WidgetFactory;
import treehou.se.habit.util.ConnectionFactory;
import treehou.se.habit.util.RxUtil;
import treehou.se.habit.util.logging.Logger;

public class PagePresenter extends RxPresenter implements PageContract.Presenter {

    private static final String TAG = PagePresenter.class.getSimpleName();

    private PageContract.View view;
    private Context context;

    private ConnectionFactory connectionFactory;
    private ServerLoaderFactory serverLoaderFactory;
    private WidgetFactory widgetFactory;
    private Logger log;

    private List<OHWidget> widgets = new ArrayList<>();
    private List<WidgetFactory.IWidgetHolder> widgetHolders = new ArrayList<>();
    private boolean initialized = false;
    private Realm realm;

    private ServerDB server;
    private OHLinkedPage page;

    private Bundle args;

    @Inject
    public PagePresenter(PageContract.View view, Context context, @Named("arguments") Bundle args, Logger log, WidgetFactory widgetFactory, ServerLoaderFactory serverLoaderFactory, ConnectionFactory connectionFactory, Realm realm) {
        this.view = view;
        this.context = context;
        this.log = log;
        this.widgetFactory = widgetFactory;
        this.serverLoaderFactory = serverLoaderFactory;
        this.connectionFactory = connectionFactory;
        this.realm = realm;
        this.args = args;
    }

    @Override
    public void load(Bundle savedData) {

        Gson gson = GsonHelper.createGsonBuilder();

        long serverId = args.getLong(PageContract.ARG_SERVER);
        String jPage = args.getString(PageContract.ARG_PAGE);

        server = ServerDB.load(realm, serverId);
        page = gson.fromJson(jPage, OHLinkedPage.class);

        initialized = false;
        if(savedData != null && savedData.containsKey(PageContract.STATE_PAGE)) {
            jPage = savedData.getString(PageContract.STATE_PAGE);
            OHLinkedPage savedPage = gson.fromJson(jPage, OHLinkedPage.class);
            if(savedPage.getId().equals(page.getId())) {
                page = savedPage;
                initialized = true;
            }
        }
    }


    @Override
    public void subscribe() {
        updatePage(page, true);
        if(!initialized && server != null) {
            requestPageUpdate();
        }
        initialized = true;

        // Start listening for server updates
        if (supportsLongPolling()) {
            createLongPoller();
        }
    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void unload() {
    }

    @Override
    public void save(Bundle savedData) {
        savedData.putSerializable(PageContract.STATE_PAGE, GsonHelper.createGsonBuilder().toJson(page));
    }


    private Action1<Throwable> dataLoadError = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            log.e(TAG, "Error when requesting page ", throwable);
            view.showLostServerConnectionMessage();
            view.closeView();
        }
    };

    /**
     * Check if android device supports long polling.
     * @return true if long polling is supported, else false.
     */
    private boolean supportsLongPolling(){
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && server != null;
    }

    /**
     * Request page from server.
     */
    private void requestPageUpdate(){
        final IServerHandler serverHandler = connectionFactory.createServerHandler(server.toGeneric(), context);

        serverHandler.requestPageRx(page)
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ohLinkedPage -> {
                    log.d(TAG, "Received update " + ohLinkedPage.getWidgets().size() + " widgets from  " + page.getLink());
                    updatePage(ohLinkedPage);
                }, dataLoadError);
    }

    /**
     * Create longpoller listening for updates of page.
     *
     * @return
     */
    private Subscription createLongPoller() {
        final long serverId = server.getId();

        OHServer server = serverLoaderFactory.loadServer(realm, serverId);
        final IServerHandler serverHandler = connectionFactory.createServerHandler(server, context);
        return serverHandler.requestPageUpdatesRx(page)
                .compose(this.bindToLifecycle())
                .compose(RxUtil.newToMainSchedulers())
                .subscribe(this::updatePage, dataLoadError);
    }


    /**
     * Update page.
     *
     * Recreate all widgets needed.
     *
     * @param page the page to show.
     * @param force true to invalidate all widgets, false to do if needed.
     */
    private synchronized void updatePage(final OHLinkedPage page, boolean force){
        if(page == null || page.getWidgets() == null) return;

        this.page = page;
        final List<OHWidget> pageWidgets = page.getWidgets();
        boolean invalidate = !canBeUpdated(widgets, pageWidgets) || force;

        if(invalidate) {
            invalidateWidgets(pageWidgets);
        } else {
            updateWidgets(pageWidgets);
        }

        view.updatePage(page);
    }

    /**
     * Update page.
     * Invalidate widgets if possible.
     *
     * @param page
     */
    private synchronized void updatePage(final OHLinkedPage page){
        updatePage(page, false);
    }

    /**
     * Check if item can be updgraded without replacing widget.
     * @param widget1 first widget to check.
     * @param widget2 second widget to check.
     * @return true if widget can be updated, else false.
     */
    public boolean canBeUpdated(OHWidget widget1, OHWidget widget2){
        if(!widget1.getType().equals(widget2.getType())){
            return false;
        }

        if(widget1.getItem() == null && widget2.getItem() == null){
            return true;
        }

        if(widget1.getItem() != null && widget2.getItem() != null){
            return widget1.getItem().getType().equals(widget2.getItem().getType());
        }

        return false;
    }

    /**
     * Check if item can be updgraded without replacing widget.
     * @param widgetSet1 first widget set to check.
     * @param widgetSet2 second widget set to check.
     * @return true if widget can be updated, else false.
     */
    public boolean canBeUpdated(List<OHWidget> widgetSet1, List<OHWidget> widgetSet2){
        boolean invalidate = widgetSet1.size() != widgetSet2.size();
        if(!invalidate){
            for(int i=0; i < widgetSet1.size(); i++) {
                OHWidget currentWidget = widgetSet1.get(i);
                OHWidget newWidget = widgetSet2.get(i);

                // TODO check if widget needs updating
                if(!canBeUpdated(currentWidget, newWidget)){
                    log.d(TAG, "Widget " + currentWidget.getType() + " " + currentWidget.getLabel() + " needs update");
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Invalidate all widgets in page.
     * @param pageWidgets the widgets to update.
     */
    private void invalidateWidgets(List<OHWidget> pageWidgets){
        log.d(TAG, "Invalidate widgets");
        widgetHolders.clear();

        for (OHWidget widget : pageWidgets) {
            try {
                WidgetFactory.IWidgetHolder widgetView = widgetFactory.createWidget(context, server.toGeneric(), page, widget, null);
                widgetHolders.add(widgetView);
            } catch (Exception e) {
                log.w(TAG, "Create widget failed", e);
            }
        }
        view.setWidgets(widgetHolders);

        widgets.clear();
        widgets.addAll(pageWidgets);
    }

    /**
     * Update widgets in page.
     * @param pageWidgets the data to update widgets with.
     */
    private void updateWidgets(List<OHWidget> pageWidgets){
        for (int i=0; i < widgetHolders.size(); i++) {

            try {
                WidgetFactory.IWidgetHolder holder = widgetHolders.get(i);

                log.d(TAG, "updating widget " + holder.getClass().getSimpleName());
                OHWidget newWidget = pageWidgets.get(i);

                holder.update(newWidget);
            } catch (Exception e) {
                log.w(TAG, "Updating widget failed", e);
            }
        }
    }
}
