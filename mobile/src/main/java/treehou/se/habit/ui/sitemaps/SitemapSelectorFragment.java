package treehou.se.habit.ui.sitemaps;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback;
import se.treehou.ng.ohcommunicator.services.callbacks.OHResponse;
import treehou.se.habit.R;
import treehou.se.habit.ui.adapter.SitemapAdapter;

public class SitemapSelectorFragment extends Fragment {

    private static final String TAG = "SitemapSelectorFragment";

    @Bind(R.id.list) RecyclerView mListView;

    private SitemapAdapter mSitemapAdapter;

    public static SitemapSelectorFragment newInstance() {
        SitemapSelectorFragment fragment = new SitemapSelectorFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SitemapSelectorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSitemapAdapter = new SitemapAdapter(getActivity());
        mSitemapAdapter.setSelectorListener(sitemapSelectListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sitemap_selector, container, false);
        ButterKnife.bind(this, rootView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);

        mListView.setLayoutManager(gridLayoutManager);
        mListView.setItemAnimator(new DefaultItemAnimator());
        mListView.setAdapter(mSitemapAdapter);

        return rootView;
    }

    SitemapAdapter.OnSitemapSelectListener sitemapSelectListener = new SitemapAdapter.OnSitemapSelectListener() {
        @Override
        public void onSitemapSelect(OHSitemap sitemap) {
            if(getTargetFragment() != null){
                ((SitemapAdapter.OnSitemapSelectListener) getTargetFragment()).onSitemapSelect(sitemap);
            }else {
                ((SitemapAdapter.OnSitemapSelectListener) getActivity()).onSitemapSelect(sitemap);
            }
        }

        @Override
        public void onErrorClicked(OHServer server) {
            requestSitemap(server);
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        mSitemapAdapter.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * Request sitemaps for server.
     * @param server the server to request sitemap for.
     */
    private void requestSitemap(final OHServer server){

        mSitemapAdapter.setServerState(server, SitemapAdapter.SitemapItem.STATE_LOADING);
        Openhab.instance(server).requestSitemaps(new OHCallback<List<OHSitemap>>() {
            @Override
            public void onUpdate(OHResponse<List<OHSitemap>> items) {
                List<OHSitemap> sitemaps = items.body();
                for (OHSitemap sitemap : sitemaps) {
                    sitemap.setServer(server);
                    if (!mSitemapAdapter.contains(sitemap)) {
                        mSitemapAdapter.add(sitemap);
                    } else if (OHSitemap.isLocal(sitemap)) {
                        mSitemapAdapter.remove(sitemap);
                        mSitemapAdapter.add(sitemap);
                    }
                }
                mSitemapAdapter.notifyDataSetChanged();
                Log.d(TAG, "Received " + sitemaps.size() + " servers");
            }

            @Override
            public void onError() {
                mSitemapAdapter.setServerState(server, SitemapAdapter.SitemapItem.STATE_ERROR);
            }
        });
    }
}
