package treehou.se.habit.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import io.realm.Realm;
import io.realm.RealmResults;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.OHRealm;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.ui.adapter.ServersAdapter;
import treehou.se.habit.ui.settings.SetupServerFragment;

public class ServersFragment extends Fragment {

    private static final String TAG = "ServersFragment";

    private static final String STATE_INITIALIZED = "state_initialized";

    private ViewGroup container;

    private RecyclerView lstServer;
    private View viwEmpty;

    private ServersAdapter serversAdapter;

    private boolean initialized = false;
    private Realm realm;

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

        realm = Realm.getDefaultInstance();

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
                startNewServerFlow();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void setup(){
        RealmResults<ServerDB> servers = realm.allObjects(ServerDB.class);
        Log.d(TAG, "Loaded " + servers.size() + " servers");

        serversAdapter = new ServersAdapter(getContext(), servers);
        serversAdapter.setItemListener(new ServersAdapter.ItemListener() {
            @Override
            public void onItemClickListener(ServersAdapter.ServerHolder serverHolder) {
                final ServerDB server = serversAdapter.getItem(serverHolder.getAdapterPosition());
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.page_container, SetupServerFragment.newInstance(server.getId()))
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public boolean onItemLongClickListener(final ServersAdapter.ServerHolder serverHolder) {

                final ServerDB server = serversAdapter.getItem(serverHolder.getAdapterPosition());
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
        initialized = true;
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
                startNewServerFlow();
                break;
            case R.id.action_scan_for_server:
                openServerScan();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Launch flow for creating new server.
     */
    private void startNewServerFlow(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.page_container, SetupServerFragment.newInstance())
                .addToBackStack(null)
                .commit();
    }

    /**
     * Launch flow used to scan for server on network.
     */
    private void openServerScan(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.page_container, ScanServersFragment.newInstance())
                .addToBackStack(null)
                .commit();
    }

    /**
     * Launchs flow asking user to remove or keep server.
     * @param serverHolder holder that triggered flow.
     * @param server the server to remove.
     */
    private void showRemoveDialog(final ServersAdapter.ServerHolder serverHolder, final ServerDB server){
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.remove_server_question)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                server.removeFromRealm();
                                serversAdapter.notifyItemRemoved(serverHolder.getAdapterPosition());
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
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

}
