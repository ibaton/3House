package treehou.se.habit.ui.sitemaps;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import se.treehou.ng.ohcommunicator.connector.GsonHelper;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.module.ServerLoaderFactory;
import treehou.se.habit.ui.widgets.WidgetFactory;
import treehou.se.habit.util.ConnectionFactory;
import treehou.se.habit.util.RxUtil;

public class PageFragment extends RxFragment {

    private static final String TAG = "PageFragment";

    // Arguments
    private static final String ARG_PAGE    = "ARG_PAGE";
    private static final String ARG_SERVER  = "ARG_SERVER";

    private static final String STATE_PAGE = "STATE_PAGE";

    @BindView(R.id.lou_widgets) LinearLayout louFragments;

    @Inject ConnectionFactory connectionFactory;
    @Inject ServerLoaderFactory serverLoaderFactory;
    @Inject WidgetFactory widgetFactory;

    private ServerDB server;
    private OHLinkedPage page;

    private List<OHWidget> widgets = new ArrayList<>();
    private List<WidgetFactory.IWidgetHolder> widgetHolders = new ArrayList<>();

    private Unbinder unbinder;

    private boolean initialized = false;

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

        ((HabitApplication) getActivity().getApplication()).component().inject(this);

        Realm realm = Realm.getDefaultInstance();

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
                    Log.d(TAG, "Received update " + ohLinkedPage.getWidgets().size() + " widgets from  " + page.getLink());
                    updatePage(ohLinkedPage);
                }, throwable -> {
                    Log.e(TAG, "Error when requesting page ", throwable);
                    Toast.makeText(getActivity(), R.string.lost_server_connection, Toast.LENGTH_LONG).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                });
    }

    /**
     * Create longpoller listening for updates of page.
     *
     * @return
     */
    private Subscription createLongPoller() {
        final long serverId = server.getId();

        Realm realm = Realm.getDefaultInstance();
        OHServer server = serverLoaderFactory.loadServer(realm, serverId);
        realm.close();
        final IServerHandler serverHandler = connectionFactory.createServerHandler(server, getActivity());
        return serverHandler.requestPageUpdatesRx(server, page)
                .compose(this.bindToLifecycle())
                .compose(RxUtil.newToMainSchedulers())
                .subscribe(this::updatePage);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(!initialized && server != null) {
            requestPageUpdate();
        }
        initialized = true;

        updatePage(page);

        // Start listening for server updates
        if (supportsLongPolling()) {
            createLongPoller();
        }
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
     * @param page
     */
    private synchronized void updatePage(final OHLinkedPage page){
        this.page = page;

        final List<OHWidget> pageWidgets = page.getWidgets();
        boolean invalidate = pageWidgets.size() != widgets.size();
        if(!invalidate){
            for(int i=0; i < widgets.size(); i++) {
                OHWidget currentWidget = widgets.get(i);
                OHWidget newWidget = pageWidgets.get(i);

                // TODO check if widget needs updating
                //if(currentWidget.needUpdate(newWidget)){
                    Log.d(TAG, "Widget " + currentWidget.getType() + " " + currentWidget.getLabel() + " needs update");
                    invalidate = true;
                    break;
                //}
            }
        }

        final boolean invalidateWidgets = invalidate;
        if(invalidateWidgets) {
            invalidateWidgets(pageWidgets);
        } else {
            updateWidgets(pageWidgets);
        }
    }

    /**
     * Invalidate all widgets in page.
     * @param pageWidgets the widgets to update.
     */
    private void invalidateWidgets(List<OHWidget> pageWidgets){
        widgetHolders.clear();
        louFragments.removeAllViews();

        for (OHWidget widget : pageWidgets) {
            try {
                WidgetFactory.IWidgetHolder result = widgetFactory.createWidget(getContext(), server.toGeneric(), page, widget, null);
                widgetHolders.add(result);
                louFragments.addView(result.getView());
            } catch (Exception e) {
                Log.w(TAG, "Create widget failed", e);
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

                Log.d(TAG, "updating widget " + holder.getClass().getSimpleName());
                OHWidget newWidget = pageWidgets.get(i);

                holder.update(newWidget);
            } catch (Exception e) {
                Log.w(TAG, "Updating widget failed", e);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(STATE_PAGE, GsonHelper.createGsonBuilder().toJson(page));
        super.onSaveInstanceState(outState);
    }
}
