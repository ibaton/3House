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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.core.OHServerWrapper;
import se.treehou.ng.ohcommunicator.core.OHSitemapWrapper;
import se.treehou.ng.ohcommunicator.core.db.OHserver;
import se.treehou.ng.ohcommunicator.core.db.OHSitemap;
import se.treehou.ng.ohcommunicator.services.Connector;
import treehou.se.habit.R;

import treehou.se.habit.connector.Communicator;
import treehou.se.habit.util.Settings;

public class SitemapListFragment extends Fragment {

    private static final String TAG = "SitemapListFragment";

    private static final String VOLLEY_TAG_SITEMAPS = "SitemapListFragmentSitemaps";
    private static final String ARG_SHOW_SITEMAP    = "showSitemap";

    private SitemapAdapter mSitemapAdapter;
    private Communicator communicator;
    private long showSitemapId = -1;

    //private OHCallback<RealmResults<OHSitemap>> responseListener = new SitemapsRequestCallbackDummy();

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

        if(savedInstanceState != null){
            showSitemapId = -1;
        }else {
            showSitemapId = getArguments().getLong(ARG_SHOW_SITEMAP);
        }

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

        Realm realm = Realm.getDefaultInstance();
        //mSitemapAdapter = new SitemapAdapter(getContext(), realm.allObjects(OHSitemap.class), true);
        mListView.setAdapter(mSitemapAdapter);
        realm.close();

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

        /*responseListener = new OHCallback<RealmResults<OHSitemap>>() {
            @Override
            public void onUpdate(OHResponse<RealmResults<OHSitemap>> items) {

                Log.w(TAG, "Sitemaps returned " + items.body().size());

            }

            @Override
            public void onError() {
                Log.w(TAG, "No server to connect to");
            }
        };*/

        /*Realm realm = Realm.getDefaultInstance();
        RealmResults<OHserver> servers = Realm.getDefaultInstance().allObjects(OHserver.class);
        for(final OHserver server : servers){
            requestSitemap(server);
        }
        realm.close();*/
    }

    @Override
    public void onPause() {
        super.onPause();

        // Clear pending callbacks
        //responseListener = new SitemapsRequestCallbackDummy();
    }

    /**
     * Handle callbacks for server sitemaps.
     */
    interface SitemapsRequestCallback {
        void onSuccess(OHServerWrapper server, List<OHSitemapWrapper> sitemaps);
        void onFailure(OHServerWrapper server, String message);
    }

    /*class SitemapsRequestCallbackDummy implements OHCallback<RealmResults<OHSitemap>> {

        @Override
        public void onUpdate(OHResponse<RealmResults<OHSitemap>> items) {}

        @Override
        public void onError() {}
    }*/

    /**
     * Request and load sitemaps for server.
     * Prioritize sitemaps on local network.
     *
     * @param server
     */
    private void requestSitemap(final OHserver server){

        mSitemapAdapter.setServerState(server, SitemapItem.STATE_LOADING);
        Connector.ServerHandler instance = Openhab.instance(server.getId());

        /*instance.registerSitemapsListener(new OHCallback<RealmResults<OHSitemap>>() {
            @Override
            public void onUpdate(OHResponse<RealmResults<OHSitemap>> sitemaps) {
                Log.d(TAG, "Received response");
                responseListener.onUpdate(new OHResponse.Builder<>(sitemaps.body()).build());
            }

            @Override
            public void onError() {
                Log.d(TAG, "Error Request sitemaps");
            }
        });*/
    }

    private class SitemapItem{
        public static final int STATE_SUCCESS = 0;
        public static final int STATE_LOADING = 1;
        public static final int STATE_ERROR = 2;

        public long serverId;
        public int state = STATE_LOADING;
        public List<Long> sitemaps = new ArrayList<>();

        public SitemapItem(OHserver server) {
            this.serverId = server.getId();
        }

        public void addItem(long sitemap){
            sitemaps.add(sitemap);
            state = STATE_SUCCESS;
        }
    }

    private class SitemapAdapter extends RecyclerView.Adapter<SitemapAdapter.SitemapBaseHolder>{

        protected LayoutInflater inflater;
        protected Context context;
        private final RealmChangeListener listener;

        // Server id sitemapItem map
        private Map<Long, SitemapItem> items = new HashMap<>();

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
            public long sitemapId;

            public GetResult(SitemapItem item, long sitemapId) {
                this.sitemapId = sitemapId;
                this.item = item;
            }
        }

        public SitemapAdapter(Context context, /*RealmResults<OHSitemap> realmResults, */boolean automaticUpdate) {
            if (context == null) {
                throw new IllegalArgumentException("Context cannot be null");
            }
            this.context = context;
            this.inflater = LayoutInflater.from(context);
            this.listener = (!automaticUpdate) ? null : new RealmChangeListener() {
                @Override
                public void onChange() {
                    notifyDataSetChanged();
                }
            };

            /*if (listener != null && realmResults != null) {
                realmResults.addChangeListener(listener);
            }*/
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


            OHserver serverDB = OHserver.load(item.item.serverId);
            OHSitemap sitemap = OHSitemap.load(item.sitemapId);
            if(SitemapItem.STATE_SUCCESS == type){
                SitemapHolder holder = (SitemapHolder) sitemapHolder;

                holder.lblName.setText(sitemap.getLabel());
                holder.lblServer.setText(serverDB.getDisplayName(getActivity(), serverDB));

                sitemapHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OHSitemap sitemap = OHSitemap.load(item.sitemapId);

                        Settings settings = Settings.instance(getActivity());
                        settings.setDefaultSitemap(sitemap);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.page_container, SitemapFragment.newInstance(sitemap))
                                .addToBackStack(null)
                                .commit();
                    }
                });
            }else if(SitemapItem.STATE_LOADING == type){
                SitemapLoadHolder holder = (SitemapLoadHolder) sitemapHolder;
                holder.lblServer.setText(OHserver.getDisplayName(getActivity(), serverDB));
            }else if(SitemapItem.STATE_ERROR == type){
                SitemapErrorHolder holder = (SitemapErrorHolder) sitemapHolder;
                holder.lblServer.setText(OHserver.getDisplayName(getActivity(), serverDB));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final OHserver serverDB = OHserver.load(item.item.serverId);
                        requestSitemap(serverDB);
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
                    for(long sitemap : item.sitemaps){
                        if(count == position){
                            result = new GetResult(item, sitemap);
                            return result;
                        }
                        count++;
                    }
                }else{
                    if(count == position){
                        result = new GetResult(item, -1);
                        break;
                    }
                    count++;
                }
            }

            return result;
        }

        public void addAll(List<Long> sitemapIds){
            for(long sitemapId : sitemapIds){
                add(sitemapId);
            }
        }

        public void add(long sitemapId) {
            add(OHSitemap.load(sitemapId));
        }

        public void add(OHSitemap sitemap) {
            SitemapItem item = items.get(sitemap.getServer().getId());
            if(item == null){
                item = new SitemapItem(sitemap.getServer());
                items.put(item.serverId, item);
            }

            int count = getItemCount();
            item.addItem(sitemap.getId());

            notifyDataSetChanged();
        }

        public void remove(long sitemapId) {
            int pos = findPosition(sitemapId);
            remove(sitemapId, pos);
        }

        public void remove(long sitemap, int position) {
            OHserver serverDB = OHSitemap.load(sitemap).getServer();
            SitemapItem item = items.get(serverDB.getId());
            if(item == null){
                return;
            }

            item.sitemaps.remove(sitemap);
            notifyItemRemoved(position);
        }

        private int findPosition(final long sitemapId){
            int count = 0;
            for(SitemapItem item : items.values()){
                if(SitemapItem.STATE_SUCCESS == item.state){
                    for(long sitemap : item.sitemaps){
                        if(sitemap == sitemapId){
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

        public void setServerState(OHserver server, int state) {
            SitemapItem item = items.get(server);
            if(item == null){
                item = new SitemapItem(server);
                items.put(server.getId(), item);
            }
            item.state = state;

            notifyDataSetChanged();
        }

        public boolean contains(OHSitemapWrapper sitemap){
            return items.containsKey(sitemap.getServer()) && items.get(sitemap.getServer()).sitemaps.contains(sitemap);
        }
    }
}
