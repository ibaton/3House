package treehou.se.habit.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback;
import se.treehou.ng.ohcommunicator.services.callbacks.OHResponse;
import treehou.se.habit.R;

import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.util.Settings;

public class SitemapListFragment extends Fragment {

    private static final String TAG = "SitemapListFragment";

    private static final String VOLLEY_TAG_SITEMAPS = "SitemapListFragmentSitemaps";
    private static final String ARG_SHOW_SITEMAP    = "showSitemap";

    private Realm realm;

    private SitemapAdapter mSitemapAdapter;
    private Communicator communicator;
    private long showSitemapId = -1;
    private ServerDB server;

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
     * @param sitemap id of sitemap to load
     * @return Fragment
     */
    public static SitemapListFragment newInstance(long sitemap) {
        SitemapListFragment fragment = new SitemapListFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SHOW_SITEMAP, sitemap);
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

        realm = Realm.getDefaultInstance();

        if(savedInstanceState != null) showSitemapId = -1;
        else showSitemapId = getArguments().getLong(ARG_SHOW_SITEMAP);

        communicator = Communicator.instance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sitemaplist, container, false);

        setupActionBar();

        RecyclerView mListView = (RecyclerView) view.findViewById(R.id.list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        mListView.setLayoutManager(gridLayoutManager);
        mListView.setItemAnimator(new DefaultItemAnimator());

        mSitemapAdapter = new SitemapAdapter(getContext());
        mListView.setAdapter(mSitemapAdapter);

        return view;
    }

    /**
     * Setup actionbar.
     */
    private void setupActionBar(){
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(R.string.sitemaps);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        RealmResults<ServerDB> servers = realm.allObjects(ServerDB.class);
        Log.d(TAG, "Requesting sitemaps for " + servers.size() + " servers");
        for(final ServerDB server : servers){
            requestSitemap(server);
        }
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

        Log.d(TAG, "Requesting Sitemap " + server.getName());
        mSitemapAdapter.setServerState(server, SitemapItem.STATE_LOADING);
        Openhab.instance(server.toGeneric()).requestSitemaps(new OHCallback<List<OHSitemap>>() {
            @Override
            public void onUpdate(final OHResponse<List<OHSitemap>> sitemaps) {
                Log.d(TAG, "Received response sitemaps " + sitemaps.body().size());
                for(OHSitemap sitemap : sitemaps.body()) {
                    sitemap.setServer(server.toGeneric());
                    mSitemapAdapter.add(server, sitemap);
                }
            }

            @Override
            public void onError() {
                Log.d(TAG, "Error Request sitemaps");
            }
        });
    }

    private class SitemapItem{
        public static final int STATE_SUCCESS = 0;
        public static final int STATE_LOADING = 1;
        public static final int STATE_ERROR = 2;

        public ServerDB server;
        public int state = STATE_LOADING;
        public List<OHSitemap> sitemaps = new ArrayList<>();

        public SitemapItem(ServerDB server) {
            this.server = server;
        }

        public void addItem(OHSitemap sitemap){
            sitemaps.add(sitemap);
            state = STATE_SUCCESS;
        }
    }

    private class SitemapAdapter extends RecyclerView.Adapter<SitemapAdapter.SitemapBaseHolder>{

        protected LayoutInflater inflater;
        protected Context context;

        private Map<ServerDB, SitemapItem> items = new HashMap<>();

        public class SitemapBaseHolder extends RecyclerView.ViewHolder {

            public TextView lblServer;

            public SitemapBaseHolder(View itemView) {
                super(itemView);
                lblServer = (TextView) itemView.findViewById(R.id.lbl_server);
            }
        }

        public class SitemapHolder extends SitemapBaseHolder {
            public TextView lblName;

            public SitemapHolder(View view) {
                super(view);

                lblName = (TextView) itemView.findViewById(R.id.lbl_sitemap);
            }
        }

        public class SitemapErrorHolder extends SitemapBaseHolder {
            public SitemapErrorHolder(View view) {
                super(view);
            }
        }

        public class SitemapLoadHolder extends SitemapBaseHolder {
            public SitemapLoadHolder(View view) {
                super(view);
            }
        }

        public class GetResult {

            public SitemapItem item;
            public OHSitemap sitemap;

            public GetResult(SitemapItem item, OHSitemap sitemap) {
                this.sitemap = sitemap;
                this.item = item;
            }
        }

        public SitemapAdapter(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context cannot be null");
            }
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public SitemapBaseHolder onCreateViewHolder(ViewGroup viewGroup, int type) {

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            if(SitemapItem.STATE_SUCCESS == type){
                View itemView = inflater.inflate(R.layout.item_sitemap, null);
                return new SitemapHolder(itemView);
            }else if(SitemapItem.STATE_LOADING == type){
                View itemView = inflater.inflate(R.layout.item_sitemap_load, null);
                return new SitemapLoadHolder(itemView);
            }else {
                View serverLoadFail = inflater.inflate(R.layout.item_sitemap_failed, null);
                return new SitemapErrorHolder(serverLoadFail);
            }
        }

        @Override
        public void onBindViewHolder(final SitemapBaseHolder sitemapHolder, int position) {

            int type = getItemViewType(position);
            final GetResult item = getItem(position);

            final OHSitemap sitemap = item.sitemap;
            final ServerDB server = item.item.server;
            if(SitemapItem.STATE_SUCCESS == type){
                SitemapHolder holder = (SitemapHolder) sitemapHolder;

                holder.lblName.setText(sitemap.getLabel());
                holder.lblServer.setText(server.getDisplayName());

                sitemapHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Settings settings = Settings.instance(getActivity());
                        settings.setDefaultSitemap(sitemap);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.page_container, SitemapFragment.newInstance(server, sitemap))
                                .addToBackStack(null)
                                .commit();
                    }
                });
            }else if(SitemapItem.STATE_LOADING == type){
                SitemapLoadHolder holder = (SitemapLoadHolder) sitemapHolder;
                holder.lblServer.setText(server.getDisplayName());
            }else if(SitemapItem.STATE_ERROR == type){
                SitemapErrorHolder holder = (SitemapErrorHolder) sitemapHolder;
                holder.lblServer.setText(server.getDisplayName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*final OHServer serverDB = OHServer.load(item.item.serverId);
                        requestSitemap(serverDB);*/
                    }
                });
            }
        }

        @Override
        public int getItemViewType(int position) {
            int count = 0;
            for(SitemapItem item : items.values()){
                if(SitemapItem.STATE_SUCCESS == item.state){
                    if(position >= count && position < (count+item.sitemaps.size())){
                        return SitemapItem.STATE_SUCCESS;
                    }
                    count += item.sitemaps.size();
                }else if(SitemapItem.STATE_ERROR == item.state){
                    if(count == position){
                        return SitemapItem.STATE_ERROR;
                    }
                    count++;
                }else if(SitemapItem.STATE_LOADING == item.state){
                    if(count == position){
                        return SitemapItem.STATE_LOADING;
                    }
                    count++;
                }
            }

            return SitemapItem.STATE_LOADING;
        }

        @Override
        public int getItemCount() {

            int count = 0;
            for(SitemapItem item : items.values()){
                if(item.state == SitemapItem.STATE_SUCCESS){
                    count += item.sitemaps.size();
                }else{
                    count++;
                }
            }

            return count;
        }

        /**
         * Returns item at a certain position
         *
         * @param position item to grab item for
         * @return
         */
        public GetResult getItem(int position) {
            GetResult result = null;
            int count = 0;
            for(SitemapItem item : items.values()){
                if(SitemapItem.STATE_SUCCESS == item.state){
                    for(OHSitemap sitemap : item.sitemaps){
                        if(count == position){
                            result = new GetResult(item, sitemap);
                            return result;
                        }
                        count++;
                    }
                }else{
                    if(count == position){
                        result = new GetResult(item, null);
                        break;
                    }
                    count++;
                }
            }

            return result;
        }

        public void addAll(ServerDB server, List<OHSitemap> sitemapIds){
            for(OHSitemap sitemap : sitemapIds){
                add(server, sitemap);
            }
        }

        public void add(ServerDB server, OHSitemap sitemap) {
            SitemapItem item = items.get(server);
            if(item == null){
                item = new SitemapItem(server);
                items.put(item.server, item);
            }
            item.addItem(sitemap);

            notifyDataSetChanged();
        }

        public void remove(OHSitemap sitemap) {
            int pos = findPosition(sitemap);
            remove(sitemap, pos);
        }

        public void remove(OHSitemap sitemap, int position) {
            SitemapItem item = null; // TODO items.get(serverDB.getId());
            if(sitemap == null){
                return;
            }

            item.sitemaps.remove(sitemap);
            notifyItemRemoved(position);
        }

        private int findPosition(final OHSitemap sitemap){
            int count = 0;
            for(SitemapItem item : items.values()){
                if(SitemapItem.STATE_SUCCESS == item.state){
                    for(OHSitemap sitemapIter : item.sitemaps){
                        if(sitemap == sitemapIter){
                            return count;
                        }
                        count++;
                    }
                }else{
                    count++;
                }
            }
            return -1;
        }

        /**
         * Get a sitemap item. Creates a new server item if no item exists.
         * @param server server to get sitemap item for.
         * @return
         */
        private SitemapItem getItem(ServerDB server){
            SitemapItem item = items.get(server);
            if(item == null){
                item = new SitemapItem(server);
                items.put(server, item);
            }
            return item;
        }

        public void setServerState(ServerDB server, int state) {
            SitemapItem item = getItem(server);
            item.state = state;

            notifyDataSetChanged();
        }

        public boolean contains(OHSitemap sitemap){
            return items.containsKey(sitemap.getServer()) && items.get(sitemap.getServer()).sitemaps.contains(sitemap);
        }
    }
}
