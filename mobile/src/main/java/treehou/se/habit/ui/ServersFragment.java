package treehou.se.habit.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.RealmChangeListener;
import se.treehou.ng.ohcommunicator.core.db.OHserver;
import treehou.se.habit.R;
import treehou.se.habit.ui.settings.SetupServerFragment;

public class ServersFragment extends Fragment {

    private static final String TAG = "ServersFragment";

    private static final String STATE_INITIALIZED = "state_initialized";

    private ViewGroup container;

    private RecyclerView lstServer;
    private View viwEmpty;

    private boolean initialized = false;

    public static ServersFragment newInstance() {
        ServersFragment fragment = new ServersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ServersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            initialized = savedInstanceState.getBoolean(STATE_INITIALIZED, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        this.container = container;

        View rootView = inflater.inflate(R.layout.fragment_servers, container, false);

        viwEmpty = rootView.findViewById(R.id.empty);
        viwEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewServerProcess();
            }
        });

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(R.string.servers);
        }

        lstServer = (RecyclerView) rootView.findViewById(R.id.list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        lstServer.setLayoutManager(gridLayoutManager);
        lstServer.setItemAnimator(new DefaultItemAnimator());

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        setup();
    }

    private void setup(){
        /*final ServersAdapter serversAdapter = new ServersAdapter(getContext(), Realm.getDefaultInstance().allObjects(OHserver.class), true);
        serversAdapter.setItemListener(new ServersAdapter.ItemListener() {
            @Override
            public void onItemClickListener(ServersAdapter.ServerHolder serverHolder) {
                final OHserver server = serversAdapter.getItem(serverHolder.getAdapterPosition());
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.page_container, SetupServerFragment.newInstance(server.getId()))
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public boolean onItemLongClickListener(final ServersAdapter.ServerHolder serverHolder) {

                final OHserver server = serversAdapter.getItem(serverHolder.getAdapterPosition());
                new AlertDialog.Builder(getActivity())
                        .setItems(R.array.server_manager, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .replace(container.getId(), BindingsFragment.newInstance(server))
                                                .addToBackStack(null)
                                                .commit();
                                        break;
                                    case 1:
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .replace(container.getId(), InboxListFragment.newInstance(server))
                                                .addToBackStack(null)
                                                .commit();
                                        break;
                                    case 2:
                                        showRemoveDialog(serverHolder, server);
                                        break;
                                }
                            }
                        })
                        .create().show();
                return true;
            }

            @Override
            public void itemCountUpdated(int itemCount) {
                updateEmptyView(itemCount);
            }
        });
        lstServer.setAdapter(serversAdapter);

        if(!initialized && serversAdapter.getItemCount() <= 0){
            showScanServerFlow();
        }
        initialized = true;*/
    }

    private void showScanServerFlow() {
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.start_scan_question)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openServerScan();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.servers, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add_server:
                startNewServerProcess();
                break;
            case R.id.action_scan_for_server:
                openServerScan();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startNewServerProcess(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.page_container, SetupServerFragment.newInstance())
                .addToBackStack(null)
                .commit();
    }

    private void openServerScan(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.page_container, ScanServersFragment.newInstance())
                .addToBackStack(null)
                .commit();
    }

    private void showRemoveDialog(final ServersAdapter.ServerHolder serverHolder, final OHserver server){
        /*new AlertDialog.Builder(getActivity())
                .setMessage(R.string.remove_server_question)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OHRealm.realm().beginTransaction();
                        server.removeFromRealm();
                        OHRealm.realm().commitTransaction();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();*/
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_INITIALIZED, true);
        super.onSaveInstanceState(outState);
    }

    /**
     * Show empty view if no controllers exist
     */
    private void updateEmptyView(int itemCount){
        viwEmpty.setVisibility(itemCount <= 0 ? View.VISIBLE : View.GONE);
    }

    public static class ServersAdapter extends RecyclerView.Adapter<ServersAdapter.ServerHolder>{

        //private RealmResults<OHserver> realmResults;
        private Context context;
        private ItemListener itemListener = new DummyItemListener();
        private final RealmChangeListener listener;

        public class ServerHolder extends RecyclerView.ViewHolder {
            public final TextView lblName;

            public ServerHolder(View view) {
                super(view);
                lblName = (TextView) view.findViewById(R.id.lbl_server);
            }
        }

        public ServersAdapter(Context context, /*RealmResults<OHserver> realmResults, */boolean automaticUpdate) {
            if (context == null) {
                throw new IllegalArgumentException("Context cannot be null");
            }
            this.context = context;
            //this.realmResults = realmResults;
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
        public ServerHolder onCreateViewHolder(ViewGroup viewGroup, int position) {

            LayoutInflater inflater = LayoutInflater.from(context);
            View itemView = inflater.inflate(R.layout.item_server, null);

            return new ServerHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ServerHolder serverHolder, final int position) {
            /*OHserver server = realmResults.get(position);

            serverHolder.lblName.setText(OHserver.getDisplayName(context, server));
            serverHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onItemClickListener(serverHolder);
                }
            });
            serverHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return itemListener.onItemLongClickListener(serverHolder);
                }
            });*/
        }

        @Override
        public int getItemCount() {
            //return realmResults.size();
            return 0;
        }

        public OHserver getItem(int position) {
            //return realmResults.get(position);
            return null;
        }

        interface ItemListener{

            void onItemClickListener(ServerHolder serverHolder);

            boolean onItemLongClickListener(ServerHolder serverHolder);

            void itemCountUpdated(int itemCount);
        }

        public class DummyItemListener implements ItemListener {

            @Override
            public void onItemClickListener(ServerHolder serverHolder) {}

            @Override
            public boolean onItemLongClickListener(ServerHolder serverHolder) {
                return false;
            }

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
    }
}
