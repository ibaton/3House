package treehou.se.habit.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.util.Iterator;
import java.util.List;

import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback;
import se.treehou.ng.ohcommunicator.services.callbacks.OHResponse;
import treehou.se.habit.R;

public class ScanServersFragment extends Fragment {

    private static final String TAG = "ScanServersFragment";

    private ServersAdapter serversAdapter;

    private View viwEmpty;

    private OHCallback<List<OHServer>> discoveryListener;

    public static ScanServersFragment newInstance() {
        ScanServersFragment fragment = new ScanServersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ScanServersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        serversAdapter = new ServersAdapter(getActivity());
        // TODO serversAdapter.addAll(OHServer.loadAll());
        serversAdapter.setItemListener(new ServersAdapter.ItemListener() {
            @Override
            public void onItemClickListener(ServersAdapter.ServerHolder serverHolder) {
                final OHServer server = serversAdapter.getItem(serverHolder.getAdapterPosition());
                // TODO server.save();

                getFragmentManager().popBackStack();
            }

            @Override
            public void itemCountUpdated(int itemCount) {
                updateEmptyView(itemCount);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_scan_servers, container, false);

        viwEmpty = rootView.findViewById(R.id.empty);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(R.string.scan_for_server);
        }

        final RecyclerView lstServer = (RecyclerView) rootView.findViewById(R.id.list);
        lstServer.setAdapter(serversAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        lstServer.setLayoutManager(gridLayoutManager);
        lstServer.setItemAnimator(new DefaultItemAnimator());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        serversAdapter.clear();
        discoveryListener = new OHCallback<List<OHServer>>() {
            @Override
            public void onUpdate(final OHResponse<List<OHServer>> response) {
                if(isAdded()){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (OHServer server : response.body()) {
                                serversAdapter.addItem(server);
                            }
                        }
                    });
                }
            }

            @Override
            public void onError() {}
        };
        Openhab.registerServerDiscoveryListener(discoveryListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        Openhab.deregisterServerDiscoveryListener(discoveryListener);
    }

    /**
     * Show empty view if no controllers exist
     */
    private void updateEmptyView(int itemCount){
        viwEmpty.setVisibility(itemCount <= 0 ? View.VISIBLE : View.GONE);
    }

    public static class ServersAdapter extends RecyclerView.Adapter<ServersAdapter.ServerHolder>{

        private List<OHServer> items = new ArrayList<>();
        private Context context;

        private ItemListener itemListener = new DummyItemListener();

        public class ServerHolder extends RecyclerView.ViewHolder {
            public final TextView lblName;
            public final TextView lblHost;

            public ServerHolder(View view) {
                super(view);
                lblName = (TextView) view.findViewById(R.id.lbl_server);
                lblHost = (TextView) view.findViewById(R.id.lbl_host);
            }
        }

        public ServersAdapter(Context context) {
            this.context = context;
        }

        @Override
        public ServerHolder onCreateViewHolder(ViewGroup viewGroup, int position) {

            LayoutInflater inflater = LayoutInflater.from(context);
            View itemView = inflater.inflate(R.layout.item_scan_server, null);

            return new ServerHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ServerHolder serverHolder, final int position) {
            OHServer server = items.get(position);

            serverHolder.lblName.setText(server.getDisplayName());
            //serverHolder.lblHost.setText(server.getUrl());
            serverHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onItemClickListener(serverHolder);
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public OHServer getItem(int position) {
            return items.get(position);
        }

        interface ItemListener{

            void onItemClickListener(ServerHolder serverHolder);

            void itemCountUpdated(int itemCount);
        }

        public class DummyItemListener implements ItemListener {

            @Override
            public void onItemClickListener(ServerHolder serverHolder) {}

            @Override
            public void itemCountUpdated(int itemCount) {}
        }

        public void setItemListener(ItemListener itemListener) {
            if(itemListener == null){
                this.itemListener = new DummyItemListener();
                return;
            }
            this.itemListener = itemListener;
        }

        public void addItem(OHServer item) {
            if(items.contains(item)){
                return;
            }
            items.add(0, item);
            notifyItemInserted(0);
            itemListener.itemCountUpdated(items.size());
        }

        public void addAll(List<OHServer> items) {
            Iterator<OHServer> serverIterator = items.iterator();
            while (serverIterator.hasNext()) {
                OHServer serverDB = serverIterator.next();
                if(this.items.contains(serverDB)){
                    serverIterator.remove();
                }
            }

            for(OHServer item : items) {
                this.items.add(0, item);
            }
            notifyItemRangeInserted(0, items.size());
            itemListener.itemCountUpdated(items.size());
        }

        public void removeItem(int position) {
            Log.d(TAG, "removeItem: " + position);
            items.remove(position);
            notifyItemRemoved(position);
            itemListener.itemCountUpdated(items.size());
        }

        public void removeItem(OHServer item) {
            int position = items.indexOf(item);
            items.remove(position);
            itemListener.itemCountUpdated(items.size());
        }

        public void clear() {
            this.items.clear();
            notifyDataSetChanged();
            itemListener.itemCountUpdated(items.size());
        }
    }
}
