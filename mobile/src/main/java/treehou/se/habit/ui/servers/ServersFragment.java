package treehou.se.habit.ui.servers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import com.trello.rxlifecycle.components.support.RxFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.functions.Action1;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.module.ApplicationComponent;
import treehou.se.habit.ui.inbox.InboxListFragment;
import treehou.se.habit.ui.adapter.ServersAdapter;
import treehou.se.habit.ui.bindings.BindingsFragment;
import treehou.se.habit.util.Settings;

public class ServersFragment extends RxFragment {

    private static final String TAG = "ServersFragment";

    private ViewGroup container;
    @BindView(R.id.list) RecyclerView lstServer;
    @BindView(R.id.empty) View viwEmpty;
    @BindView(R.id.fab_add) FloatingActionButton fabAdd;

    @Inject Settings settings;

    private ServersAdapter serversAdapter;
    private RealmResults<ServerDB> servers;
    private Unbinder unbinder;

    private Realm realm;

    public static ServersFragment newInstance() {
        ServersFragment fragment = new ServersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ServersFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getApplicationComponent().inject(this);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.container = container;
        View rootView = inflater.inflate(R.layout.fragment_servers, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        lstServer.setLayoutManager(gridLayoutManager);
        lstServer.setItemAnimator(new DefaultItemAnimator());
        setupActionbar();

        return rootView;
    }

    protected ApplicationComponent getApplicationComponent() {
        return ((HabitApplication) getContext().getApplicationContext()).component();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupAdapter();
    }

    @Override
    public void onPause() {
        super.onPause();
        lstServer.setAdapter(null);
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
     * Hookup actionbar
     */
    private void setupActionbar(){
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(R.string.servers);
        }
        setHasOptionsMenu(true);
    }

    /**
     * Hookup server list, listening for server updates.
     */
    private void setupAdapter(){
        realm.where(ServerDB.class).findAllAsync().asObservable()
                .compose(this.bindToLifecycle())
                .subscribe(servers1 -> {
                    Log.d(TAG, "Loaded " + servers1.size() + " servers");
                    ServersFragment.this.servers = servers1;
                    updateEmptyView(servers1.size());
                    serversAdapter = new ServersAdapter(servers1);
                    serversAdapter.setItemListener(serverListener);
                    lstServer.setAdapter(serversAdapter);
                });

        if(!settings.getServerSetupAsked()){
            showScanServerFlow();
        }
    }

    /**
     * Launch flow for opening server scanning.
     */
    private void showScanServerFlow() {
        settings.setServerSetupAsked(true);
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.start_scan_question)
                .setNeutralButton(R.string.new_server, (dialog, which) -> startNewServerFlow())
                .setPositiveButton(R.string.scan, (dialog, which) -> openServerScan())
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
            case R.id.action_scan_for_server:
                openServerScan();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private ServersAdapter.ItemListener serverListener = new ServersAdapter.ItemListener() {
        @Override
        public void onItemClickListener(ServersAdapter.ServerHolder serverHolder) {
            final ServerDB server = serversAdapter.getItem(serverHolder.getAdapterPosition());
            openServerPage(server);
        }

        /**
         * Open page for editing server.
         * @param server the server to open page for.
         */
        private void openServerPage(ServerDB server){
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.page_container, ServerMenuFragment.newInstance(server.getId()))
                    .addToBackStack(null)
                    .commit();
        }

        @Override
        public boolean onItemLongClickListener(final ServersAdapter.ServerHolder serverHolder) {

            final ServerDB server = serversAdapter.getItem(serverHolder.getAdapterPosition());
            showRemoveDialog(serverHolder, server);
            return true;
        }
    };

    /**
     * Launch flow for creating new server.
     */
    @OnClick({R.id.empty, R.id.fab_add})
    public void startNewServerFlow(){
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
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    realm.beginTransaction();
                    int position = serverHolder.getAdapterPosition();
                    int i = servers.indexOf(server);
                    if(i >= 0) { servers.deleteFromRealm(i);}
                    realm.commitTransaction();
                    serversAdapter.notifyItemRemoved(position);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /**
     * Show empty view if no controllers exist
     */
    private void updateEmptyView(int itemCount){
        viwEmpty.setVisibility(itemCount <= 0 ? View.VISIBLE : View.GONE);
    }
}
