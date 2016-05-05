package treehou.se.habit.ui.sitemaps;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import se.treehou.ng.ohcommunicator.services.Connector;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.R;

import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.ui.adapter.SitemapListAdapter;
import treehou.se.habit.util.Settings;

public class SitemapListFragment extends RxFragment {

    private static final String TAG = "SitemapListFragment";

    private static final String ARG_SHOW_SITEMAP = "showSitemap";

    @Inject Settings settings;
    @Bind(R.id.list) RecyclerView listView;
    private SitemapListAdapter sitemapAdapter;
    private String showSitemap = "";
    private Realm realm;

    /**
     * Create fragment where user can select sitemap.
     *
     * @return Fragment
     */
    public static SitemapListFragment newInstance() {
        SitemapListFragment fragment = new SitemapListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Load sitemaps for servers.
     * Open provided sitemap if loaded.
     *
     * @param sitemap name of sitemap to load
     * @return Fragment
     */
    public static SitemapListFragment newInstance(String sitemap) {
        SitemapListFragment fragment = new SitemapListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SHOW_SITEMAP, sitemap);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SitemapListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getComponent().inject(this);

        realm = Realm.getDefaultInstance();

        if(savedInstanceState != null) showSitemap = "";
        else showSitemap = getArguments().getString(ARG_SHOW_SITEMAP);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sitemaplist, container, false);
        ButterKnife.bind(this, view);
        setupActionBar();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        listView.setLayoutManager(gridLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());

        sitemapAdapter = new SitemapListAdapter(getContext());
        sitemapAdapter.setSitemapSelectedListener(new SitemapListAdapter.SitemapSelectedListener() {
            @Override
            public void onSelected(ServerDB server, OHSitemap sitemap) {
                settings.setDefaultSitemap(sitemap);
                openSitemap(server, sitemap);
            }

            @Override
            public void onErrorSelected(ServerDB server) {
                requestSitemap(server);
            }
        });
        listView.setAdapter(sitemapAdapter);

        return view;
    }

    protected HabitApplication.ApplicationComponent getComponent() {
        return ((HabitApplication) getActivity().getApplication()).component();
    }

    /**
     * Open fragment showing sitemap.
     *
     * @param server the server of default sitemap.
     * @param sitemap the name of sitemap to show.
     */
    private void openSitemap(ServerDB server, OHSitemap sitemap){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.page_container, SitemapFragment.newInstance(server, sitemap))
                .addToBackStack(null)
                .commit();
    }

    /**
     * Setup actionbar.
     */
    private void setupActionBar(){
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) actionBar.setTitle(R.string.sitemaps);
    }

    @Override
    public void onResume() {
        super.onResume();

        realm.allObjects(ServerDB.class).where()
                .isNotEmpty("localurl").or().isNotEmpty("remoteurl").greaterThan("id", 0)
                .findAllAsync().asObservable()
                .compose(this.<RealmResults<ServerDB>>bindToLifecycle())
                .subscribe(new Action1<RealmResults<ServerDB>>() {
                    @Override
                    public void call(RealmResults<ServerDB> servers) {
                        Log.d(TAG, "Requesting sitemaps for " + servers.size() + " servers");
                        sitemapAdapter.clear();
                        for (final ServerDB server : servers) {
                            requestSitemap(server);
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    /**
     * Request and load sitemaps for server.
     * Prioritize sitemaps on local network.
     *
     * @param server
     */
    private void requestSitemap(final ServerDB server){
        Connector.ServerHandler serverHandler = Openhab.instance(server.toGeneric());

        Log.d(TAG, "Requesting Sitemap " + server.getName());
        sitemapAdapter.setServerState(server, SitemapListAdapter.STATE_LOADING);
        serverHandler.requestSitemapObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<List<OHSitemap>>bindToLifecycle())
                .subscribe(new Action1<List<OHSitemap>>() {
                    @Override
                    public void call(List<OHSitemap> sitemaps) {
                        Log.d(TAG, "Received response sitemaps " + sitemaps.size());
                        for (OHSitemap sitemap : sitemaps) {
                            sitemap.setServer(server.toGeneric());
                            sitemapAdapter.add(server, sitemap);

                            if(sitemap.getName().equals(showSitemap)) {
                                showSitemap = null; // Prevents sitemap from being accessed again.
                                openSitemap(server, sitemap);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "Request sitemap failed", throwable);
                        sitemapAdapter.setServerState(server, SitemapListAdapter.STATE_ERROR);
                    }
                });
    }
}
