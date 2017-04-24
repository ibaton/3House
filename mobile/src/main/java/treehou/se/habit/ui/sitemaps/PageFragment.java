package treehou.se.habit.ui.sitemaps;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import se.treehou.ng.ohcommunicator.util.GsonHelper;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.module.HasActivitySubcomponentBuilders;
import treehou.se.habit.module.ServerLoaderFactory;
import treehou.se.habit.mvp.BaseDaggerFragment;
import treehou.se.habit.ui.widgets.WidgetFactory;
import treehou.se.habit.util.ConnectionFactory;
import treehou.se.habit.util.RxUtil;
import treehou.se.habit.util.logging.Logger;

public class PageFragment extends BaseDaggerFragment<PageContract.Presenter> implements PageContract.View {

    private static final String TAG = "PageFragment";

    // Arguments
    private static final String ARG_PAGE    = "ARG_PAGE";
    private static final String ARG_SERVER  = "ARG_SERVER";

    private static final String STATE_PAGE = "STATE_PAGE";

    @BindView(R.id.lou_widgets) LinearLayout louFragments;

    @Inject ConnectionFactory connectionFactory;
    @Inject ServerLoaderFactory serverLoaderFactory;
    @Inject WidgetFactory widgetFactory;
    @Inject Logger log;
    @Inject PageContract.Presenter presenter;

    private ServerDB server;
    private OHLinkedPage page;

    private List<OHWidget> widgets = new ArrayList<>();
    private List<WidgetFactory.IWidgetHolder> widgetHolders = new ArrayList<>();

    private Unbinder unbinder;

    private boolean initialized = false;

    private Action1<Throwable> dataLoadError = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            log.e(TAG, "Error when requesting page ", throwable);
            Toast.makeText(getActivity(), R.string.lost_server_connection, Toast.LENGTH_LONG).show();
            getActivity().getSupportFragmentManager().popBackStack();
        }
    };

    /**
     * Creates a new instane of the page.
     *
     * @param server the server to connect to
     * @param page the page to visualise
     *
     * @return Fragment visualazing a page
     */
    public static PageFragment newInstance(ServerDB server, OHLinkedPage page) {
        Gson gson = GsonHelper.createGsonBuilder();

        Bundle args = new Bundle();
        args.putString(ARG_PAGE, gson.toJson(page));
        args.putLong(ARG_SERVER, server.getId());

        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public PageFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        Gson gson = GsonHelper.createGsonBuilder();

        long serverId = args.getLong(ARG_SERVER);
        String jPage = args.getString(ARG_PAGE);

        server = ServerDB.load(realm, serverId);
        page = gson.fromJson(jPage, OHLinkedPage.class);

        initialized = false;
        if(savedInstanceState != null && savedInstanceState.containsKey(STATE_PAGE)) {
            jPage = savedInstanceState.getString(STATE_PAGE);
            OHLinkedPage savedPage = gson.fromJson(jPage, OHLinkedPage.class);
            if(savedPage.getId().equals(page.getId())) {
                page = savedPage;
                initialized = true;
            }
        }
    }

    @Override
    public PageContract.Presenter getPresenter() {
        return presenter;
    }

    @Override
    protected void injectMembers(HasActivitySubcomponentBuilders hasActivitySubcomponentBuilders) {
        ((PageComponent.Builder) hasActivitySubcomponentBuilders.getFragmentComponentBuilder(PageFragment.class))
                .fragmentModule(new PageModule(this, getArguments()))
                .build().injectMembers(this);
    }

    /**
     * Setup actionbar using
     */
    private void setupActionbar(){
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        String title = page.getTitle();
        if(title == null) title = "";
        title = removeValueFromTitle(title);

        if(actionBar != null) actionBar.setTitle(title);
    }

    private String removeValueFromTitle(String title){
        return title.replaceAll("\\[.+?\\]","");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_widget, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    /**
     * Request page from server.
     */
    private void requestPageUpdate(){
        final IServerHandler serverHandler = connectionFactory.createServerHandler(server.toGeneric(), getActivity());

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
        final IServerHandler serverHandler = connectionFactory.createServerHandler(server, getActivity());
        return serverHandler.requestPageUpdatesRx(page)
                .compose(this.bindToLifecycle())
                .compose(RxUtil.newToMainSchedulers())
                .subscribe(this::updatePage, dataLoadError);
    }

    @Override
    public void onResume() {
        super.onResume();

        updatePage(page, true);
        if(!initialized && server != null) {
            requestPageUpdate();
        }
        initialized = true;

        // Start listening for server updates
        if (supportsLongPolling()) {
            createLongPoller();
        }
        setupActionbar();
    }



    /**
     * Check if android device supports long polling.
     * @return true if long polling is supported, else false.
     */
    private boolean supportsLongPolling(){
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && server != null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
        louFragments.removeAllViews();

        for (OHWidget widget : pageWidgets) {
            try {
                WidgetFactory.IWidgetHolder result = widgetFactory.createWidget(getContext(), server.toGeneric(), page, widget, null);
                widgetHolders.add(result);
                louFragments.addView(result.getView());
            } catch (Exception e) {
                log.w(TAG, "Create widget failed", e);
            }
        }
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(STATE_PAGE, GsonHelper.createGsonBuilder().toJson(page));
        super.onSaveInstanceState(outState);
    }
}
