package treehou.se.habit.ui.sitemaps.sitemaplist;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
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

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import treehou.se.habit.R;
import treehou.se.habit.module.HasActivitySubcomponentBuilders;
import treehou.se.habit.mvp.BaseDaggerFragment;
import treehou.se.habit.ui.adapter.SitemapListAdapter;
import treehou.se.habit.ui.sitemaps.sitemap.SitemapFragment;

public class SitemapListFragment extends BaseDaggerFragment<SitemapListContract.Presenter> implements SitemapListContract.View {

    private static final String TAG = "SitemapSelectFragment";

    public static final String ARG_SHOW_SITEMAP = "showSitemap";

    @Inject SitemapListContract.Presenter presenter;

    @BindView(R.id.list) RecyclerView listView;
    @BindView(R.id.empty) TextView emptyView;

    private SitemapListAdapter sitemapAdapter;
    private Unbinder unbinder;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sitemaplist_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        setupActionBar();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        listView.setLayoutManager(gridLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());

        sitemapAdapter = new SitemapListAdapter();
        sitemapAdapter.setSitemapSelectedListener(new SitemapListAdapter.SitemapSelectedListener() {

            @Override
            public void onSelected(OHServer server, OHSitemap sitemap) {
                presenter.openSitemap(server, sitemap);
            }

            @Override
            public void onErrorSelected(OHServer server) {
                Log.d(TAG, "Reloading server: " + server.getDisplayName());
                presenter.reloadSitemaps(server);
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
    @Override
    public void showSitemap(OHServer server, OHSitemap sitemap) {
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
    public void hideEmptyView(){
        emptyView.setVisibility(View.GONE);
    }

    /**
     * Clears list of sitemaps.
     */
    @Override
    public void clearList() {
        emptyView.setVisibility(View.VISIBLE);
        sitemapAdapter.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void showServerError(OHServer server) {
        sitemapAdapter.setServerState(server, SitemapListAdapter.STATE_ERROR);
    }

    @Override
    public void populateSitemaps(Pair<OHServer, List<OHSitemap>> serverSitemaps){
        OHServer server = serverSitemaps.first;
        List<OHSitemap> sitemaps = serverSitemaps.second;

        for (OHSitemap sitemap : sitemaps) {
            sitemapAdapter.add(server, sitemap);
        }
    }

    @Override
    public SitemapListContract.Presenter getPresenter() {
        return presenter;
    }

    protected void injectMembers(HasActivitySubcomponentBuilders hasActivitySubcomponentBuilders) {
        ((SitemapListComponent.Builder) hasActivitySubcomponentBuilders.getFragmentComponentBuilder(SitemapListFragment.class))
                .fragmentModule(new SitemapListModule(this, getArguments()))
                .build().injectMembers(this);
    }
}
