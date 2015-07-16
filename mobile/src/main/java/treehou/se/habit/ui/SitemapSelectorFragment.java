package treehou.se.habit.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.db.ServerDB;
import treehou.se.habit.core.Sitemap;

public class SitemapSelectorFragment extends Fragment {

    private static final String TAG = "SitemapSelectorFragment";

    private static final String VOLLEY_TAG_SITEMAPS = "SitemapListFragmentSitemaps_";

    private SitemapAdapter mSitemapAdapter;
    private Communicator communicator;

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

        communicator = Communicator.instance(getActivity());
        mSitemapAdapter = new SitemapAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sitemap_selector, container, false);
        RecyclerView mListView = (RecyclerView) rootView.findViewById(R.id.list);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);

        mListView.setLayoutManager(gridLayoutManager);
        mListView.setItemAnimator(new DefaultItemAnimator());

        mListView.setAdapter(mSitemapAdapter);

        return rootView;
    }

    public interface OnSitemapSelectListener {
        void onSitemapSelect(Sitemap sitemap);
    }

    @Override
    public void onResume() {
        super.onResume();

        mSitemapAdapter.clear();

        List<ServerDB> servers = ServerDB.getServers();
        for(final ServerDB server : servers){
            requestSitemap(server);
        }
    }

    private void requestSitemap(final ServerDB server){

        mSitemapAdapter.setServerState(server, SitemapItem.STATE_LOADING);
        communicator.requestSitemaps(VOLLEY_TAG_SITEMAPS, server, new Communicator.SitemapsRequestListener() {
            @Override
            public void onSuccess(List<Sitemap> sitemaps) {
                for (Sitemap sitemap : sitemaps) {
                    sitemap.setServer(server);
                    if (!mSitemapAdapter.contains(sitemap)) {
                        mSitemapAdapter.add(sitemap);
                    } else if (sitemap.isLocal()) {
                        mSitemapAdapter.remove(sitemap);
                        mSitemapAdapter.add(sitemap);
                    }
                }
                mSitemapAdapter.notifyDataSetChanged();
                Log.d(TAG, "Received " + sitemaps.size() + " servers");
            }

            @Override
            public void onFailure(String message) {
                if (message == null) {
                    Log.w(TAG, "No server to connect to");
                } else {
                    Log.w(TAG, "Failed to connect to server " + message + " " + server.getUrl());
                }

                mSitemapAdapter.setServerState(server, SitemapItem.STATE_ERROR);
            }
        });
    }

    class SitemapItem{

        public static final int STATE_SUCCESS = 0;
        public static final int STATE_LOADING = 1;
        public static final int STATE_ERROR = 2;

        public ServerDB server;
        public int state = STATE_LOADING;
        public List<Sitemap> sitemaps = new ArrayList<>();

        public SitemapItem(ServerDB server) {
            this.server = server;
        }

        public void addItem(Sitemap sitemap){
            sitemaps.add(sitemap);
            state = STATE_SUCCESS;
        }
    }

    class SitemapAdapter extends RecyclerView.Adapter<SitemapAdapter.SitemapBaseHolder>{

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
            public Sitemap sitemap;

            public GetResult(SitemapItem item, Sitemap sitemap) {
                this.sitemap = sitemap;
                this.item = item;
            }
        }

        public SitemapAdapter() {}

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
                View serverLoadFail = inflater.inflate(R.layout.item_sitemap_failed,null);
                return new SitemapErrorHolder(serverLoadFail);
            }
        }

        @Override
        public void onBindViewHolder(SitemapBaseHolder sitemapHolder, int position) {

            int type = getItemViewType(position);
            final GetResult item = getItem(position);

            if(SitemapItem.STATE_SUCCESS == type){
                SitemapHolder holder = (SitemapHolder) sitemapHolder;
                final Sitemap sitemap = item.sitemap;

                holder.lblName.setText(item.sitemap.getName());
                holder.lblServer.setText(item.sitemap.getServer().getName());

                sitemapHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(getTargetFragment() != null){
                            ((OnSitemapSelectListener) getTargetFragment()).onSitemapSelect(sitemap);
                        }else {
                            ((OnSitemapSelectListener) getActivity()).onSitemapSelect(sitemap);
                        }
                    }
                });
            }else if(SitemapItem.STATE_LOADING == type){
                SitemapLoadHolder holder = (SitemapLoadHolder) sitemapHolder;
                holder.lblServer.setText(item.item.server.getName());
            }else if(SitemapItem.STATE_ERROR == type){
                SitemapErrorHolder holder = (SitemapErrorHolder) sitemapHolder;
                holder.lblServer.setText(item.item.server.getName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestSitemap(item.item.server);
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

        public GetResult getItem(int position) {
            GetResult result = null;
            int count = 0;
            for(SitemapItem item : items.values()){
                if(SitemapItem.STATE_SUCCESS == item.state){
                    for(Sitemap sitemap : item.sitemaps){
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

        public void addAll(List<Sitemap> sitemaps){
            for(Sitemap sitemap : sitemaps){
                add(sitemap);
            }
        }

        public void add(Sitemap sitemap) {
            SitemapItem item = items.get(sitemap.getServer());
            if(item == null){
                item = new SitemapItem(sitemap.getServer());
                items.put(item.server, item);
            }

            int count = getItemCount();
            item.addItem(sitemap);
            Log.d(TAG, "Added sitemap " + sitemap.getServer().getName() + " " + sitemap.getName() + " precount: " + count + " postcount: " + getItemCount() + " items: " + items.size());

            notifyDataSetChanged();
        }

        public void remove(Sitemap sitemap) {
            int pos = findPosition(sitemap);
            remove(sitemap, pos);
        }

        public void remove(Sitemap sitemap, int position) {
            SitemapItem item = items.get(sitemap.getServer());
            if(item == null){
                return;
            }

            item.sitemaps.remove(sitemap);
            notifyItemRemoved(position);
        }

        private int findPosition(final Sitemap pSitemap){
            int count = 0;
            for(SitemapItem item : items.values()){
                if(SitemapItem.STATE_SUCCESS == item.state){
                    for(Sitemap sitemap : item.sitemaps){
                        if(sitemap == pSitemap){
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

        public void setServerState(ServerDB server, int state) {
            SitemapItem item = items.get(server);
            if(item == null){
                item = new SitemapItem(server);
                items.put(server, item);
            }
            item.state = state;

            notifyDataSetChanged();
        }

        public boolean contains(Sitemap sitemap){
            return items.containsKey(sitemap.getServer()) && items.get(sitemap.getServer()).sitemaps.contains(sitemap);
        }

        public void clear(){
            int last = items.size()-1;
            items.clear();
            notifyItemRangeRemoved(0, last);
        }
    }
}
