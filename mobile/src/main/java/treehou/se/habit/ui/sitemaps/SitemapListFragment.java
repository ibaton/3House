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
import android.widget.TextView;

import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.BehaviorSubject;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.R;

import treehou.se.habit.module.ServerLoaderFactory;
import treehou.se.habit.ui.adapter.SitemapListAdapter;
import treehou.se.habit.util.Settings;

public class SitemapListFragment extends RxFragment {

    private static final String TAG = "SitemapSelectFragment";

    private static final String ARG_SHOW_SITEMAP = "showSitemap";

    @Inject Settings settings;
    @Inject ServerLoaderFactory serverLoaderFactory;

    @BindView(R.id.list) RecyclerView listView;
    @BindView(R.id.empty) TextView emptyView;

    private SitemapListAdapter sitemapAdapter;
    private String showSitemap = "";
    private Realm realm;
    private Unbinder unbinder;
    private BehaviorSubject<OHServer> serverBehaviorSubject = BehaviorSubject.create();

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

        ((HabitApplication) getActivity().getApplication()).component().inject(this);

        realm = Realm.getDefaultInstance();

        if(savedInstanceState != null) showSitemap = "";
        else showSitemap = getArguments().getString(ARG_SHOW_SITEMAP);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sitemaplist, container, false);
        unbinder = ButterKnife.bind(this, view);
        setupActionBar();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        listView.setLayoutManager(gridLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());

        sitemapAdapter = new SitemapListAdapter(getContext());
        sitemapAdapter.setSitemapSelectedListener(new SitemapListAdapter.SitemapSelectedListener() {

            @Override
            public void onSelected(OHServer server, OHSitemap sitemap) {
                settings.setDefaultSitemap(sitemap);
                openSitemap(server, sitemap);
            }

            @Override
            public void onErrorSelected(OHServer server) {
                serverBehaviorSubject.onNext(server);
            }
        });
        listView.setAdapter(sitemapAdapter);

        return view;
    }

    /**
     * Open fragment showing sitemap.
     *
     * @param server the server of default sitemap.
     * @param sitemap the name of sitemap to show.
     */
    private void openSitemap(OHServer server, OHSitemap sitemap){
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

    /**
     * Clears list of sitemaps.
     */
    private void clearList() {
        emptyView.setVisibility(View.VISIBLE);
        sitemapAdapter.clear();

    }

    @Override
    public void onResume() {
        super.onResume();

        clearList();
        loadSitemapsFromServers();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    /**
     * Load servers from database and request their sitemaps.
     */
    private void loadSitemapsFromServers(){
        Observable.merge(Realm.getDefaultInstance().asObservable().compose(serverLoaderFactory.loadServersRx()),
                serverBehaviorSubject.asObservable())
                .doOnNext(server -> {
                    //sitemapAdapter.setServerState(server, SitemapListAdapter.STATE_LOADING);
                    emptyView.setVisibility(View.GONE);
                })
                .compose(serverLoaderFactory.serverToSitemap(getActivity()))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycle.bindFragment(this.lifecycle()))
                .compose(serverLoaderFactory.filterDisplaySitemaps())
                .subscribe(serverSitemaps -> {
                    OHServer server = serverSitemaps.first;
                    List<OHSitemap> sitemaps = serverSitemaps.second;

                    if(sitemaps.size() <= 0){
                        sitemapAdapter.setServerState(server, SitemapListAdapter.STATE_ERROR);
                    } else {
                        boolean autoloadLast = settings.getAutoloadSitemapRx().get();
                        for (OHSitemap sitemap : sitemaps) {
                            sitemapAdapter.add(server, sitemap);
                            if (autoloadLast && sitemap.getName().equals(showSitemap)) {
                                showSitemap = null; // Prevents sitemap from being accessed again.
                                openSitemap(server, sitemap);
                            }
                        }
                    }
                }, throwable -> {
                    Log.e(TAG, "Request sitemap failed", throwable);
                });
    }
}
