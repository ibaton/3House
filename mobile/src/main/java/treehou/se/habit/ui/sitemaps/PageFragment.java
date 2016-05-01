package treehou.se.habit.ui.sitemaps;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.connector.GsonHelper;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback;
import se.treehou.ng.ohcommunicator.services.callbacks.OHResponse;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.ui.widgets.WidgetFactory;
import treehou.se.habit.util.ThreadPool;

public class PageFragment extends Fragment {

    private static final String TAG = "PageFragment";
    private static final String PAGE_REQUEST_TAG = "PageRequestTag";

    // Arguments
    private static final String ARG_PAGE    = "ARG_PAGE";
    private static final String ARG_SERVER  = "ARG_SERVER";

    private static final String STATE_PAGE = "STATE_PAGE";

    private Realm realm;

    private ServerDB server;
    private OHLinkedPage page;

    @Bind(R.id.lou_widgets) LinearLayout louFragments;

    private WidgetFactory widgetFactory;

    private List<OHWidget> widgets = new ArrayList<>();
    private List<WidgetFactory.IWidgetHolder> widgetHolders = new ArrayList<>();

    private AsyncTask<Void, Void, Void> longPoller;

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

        realm = Realm.getDefaultInstance();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_widget, container, false);
        ButterKnife.bind(this, view);

        updatePage(page);

        if(!initialized && server != null) {
            requestPageUpdate();
        }
        initialized = true;

        return view;
    }

    private void requestPageUpdate(){
        Openhab.instance(server.toGeneric()).requestPage(page, new OHCallback<OHLinkedPage>() {
            @Override
            public void onUpdate(OHResponse<OHLinkedPage> response) {
                final OHLinkedPage linkedPage = response.body();
                Log.d(TAG, "Received update " + linkedPage.getWidgets().size() + " widgets from  " + page.getLink());
                ThreadPool.instance().submit(new Runnable() {
                    @Override
                    public void run() {
                        updatePage(linkedPage);
                    }
                });
            }

            @Override
            public void onError() {
                if(getActivity() != null) {

                    // TODO Check type of error.
                    // TODO Retry on remote server.
                    Toast.makeText(getActivity(), R.string.lost_server_connection, Toast.LENGTH_LONG).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
    }

    // TODO extract to separate class
    private AsyncTask<Void, Void, Void> createLongPoller() {
        final long serverId = server.getId();

        Realm realm = Realm.getDefaultInstance();
        OHServer server = ServerDB.load(realm, serverId).toGeneric();
        realm.close();

        return Openhab.instance(server).requestPageUpdates(server, page, new OHCallback<OHLinkedPage>() {
            @Override
            public void onUpdate(OHResponse<OHLinkedPage> items) {
                updatePage(items.body());
            }

            @Override
            public void onError() {}
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // Start listening for server updates
        // TODO Support for older versions.
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && server != null) {
            longPoller = createLongPoller();
            longPoller.execute();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * Update page.
     *
     * Recreate all widgets needed.
     *
     * @param page
     */
    private synchronized void updatePage(final OHLinkedPage page){
        Log.d(TAG, "Updating page " + page.getTitle() + " widgets " + widgets.size() + " : " + page.getWidgets().size());
        this.page = page;
        widgetFactory = new WidgetFactory(getActivity(), server, page);

        final List<OHWidget> pageWidgets = page.getWidgets();
        boolean invalidate = pageWidgets.size() != widgets.size();
        if(!invalidate){
            for(int i=0; i < widgets.size(); i++) {
                OHWidget currentWidget = widgets.get(i);
                OHWidget newWidget = pageWidgets.get(i);

                if(currentWidget.needUpdate(newWidget)){
                    Log.d(TAG, "Widget " + currentWidget.getType() + " " + currentWidget.getLabel() + " needs update");
                    invalidate = true;
                    break;
                }
            }
        }

        final boolean invalidateWidgets = invalidate;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(invalidateWidgets) {
                    Log.d(TAG, "Invalidating widgets " + pageWidgets.size() + " : " + widgets.size() + " " + page.getTitle());

                    widgetHolders.clear();
                    louFragments.removeAllViews();

                    for (OHWidget widget : pageWidgets) {
                        try {
                            WidgetFactory.IWidgetHolder result = widgetFactory.createWidget(widget, null);
                            widgetHolders.add(result);
                            louFragments.addView(result.getView());
                        } catch (Exception e) {
                            Log.w(TAG, "Create widget failed", e);
                        }
                    }
                    widgets.clear();
                    widgets.addAll(pageWidgets);
                }
                else {
                    Log.d(TAG, "updating widgets");
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
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        // Stop listening for server updates
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && longPoller != null) {
            longPoller.cancel(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(STATE_PAGE, GsonHelper.createGsonBuilder().toJson(page));
        super.onSaveInstanceState(outState);
    }
}
