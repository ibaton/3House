package treehou.se.habit.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import treehou.se.habit.Constants;
import treehou.se.habit.R;
import treehou.se.habit.core.db.ServerDB;
import treehou.se.habit.ui.settings.SetupServerFragment;

public class ServersFragment extends Fragment {

    private static final String TAG = "ServersFragment";

    private ServersAdapter serversAdapter;
    private ViewGroup container;

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

        serversAdapter = new ServersAdapter(getActivity());
        serversAdapter.addAll(ServerDB.getServers());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        this.container = container;

        View rootView = inflater.inflate(R.layout.fragment_servers, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(R.string.servers);
        }

        final RecyclerView lstServer = (RecyclerView) rootView.findViewById(R.id.list);
        lstServer.setAdapter(serversAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        lstServer.setLayoutManager(gridLayoutManager);
        lstServer.setItemAnimator(new DefaultItemAnimator());
        lstServer.setAdapter(serversAdapter);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        serversAdapter.clear();
        serversAdapter.addAll(ServerDB.getServers());

        // Initialize demo server first time starting
        SharedPreferences preferences = getActivity().getSharedPreferences(Constants.PREFERENCE_SERVER, Context.MODE_PRIVATE);
        if(preferences.getBoolean(Constants.PREF_INIT_SETUP, true)){
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean(Constants.PREF_INIT_SETUP,false);
            if(serversAdapter.getItemCount() == 0){
                ServerDB demoServer = new ServerDB();
                demoServer.setName("Demo");
                demoServer.setRemoteUrl("http://demo.openhab.org:8080");
                demoServer.save();
                serversAdapter.addItem(demoServer);
            }
            edit.apply();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.servers, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add_server:
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.page_container, SetupServerFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public class ServersAdapter extends RecyclerView.Adapter<ServersAdapter.ServerHolder>{

        private List<ServerDB> items = new ArrayList<>();
        private Context context;

        public class ServerHolder extends RecyclerView.ViewHolder {
            public final TextView lblName;

            public ServerHolder(View view) {
                super(view);
                lblName = (TextView) view.findViewById(R.id.lbl_server);
            }
        }

        public ServersAdapter(Context context) {
            this.context = context;
        }

        @Override
        public ServerHolder onCreateViewHolder(ViewGroup viewGroup, int position) {

            LayoutInflater inflater = LayoutInflater.from(context);
            View itemView = inflater.inflate(R.layout.item_server, null);

            return new ServerHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ServerHolder serverHolder, final int position) {
            final ServerDB server = items.get(position);

            serverHolder.lblName.setText(server.getDisplayName(context));
            serverHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.page_container, SetupServerFragment.newInstance(server))
                            .addToBackStack(null)
                            .commit();
                }
            });
            serverHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
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
                                            showRemoveDialog(serverHolder, server);
                                            break;
                                    }
                                }
                            })
                            .create().show();

                    return true;
                }
            });
        }

        private void showRemoveDialog(final ServerHolder serverHolder, final ServerDB server){
            new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.remove_server_question)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            serversAdapter.removeItem(serverHolder.getAdapterPosition());
                            server.delete();
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public ServerDB getItem(int position) {
            return items.get(position);
        }

        public void addItem(ServerDB item) {
            items.add(0, item);
            notifyItemInserted(0);
        }

        public void removeItem(int position) {
            Log.d(TAG, "removeItem: " + position);
            items.remove(position);
            notifyItemRemoved(position);
        }

        public void removeItem(ServerDB item) {
            int position = items.indexOf(item);
            items.remove(position);
        }

        public void clear() {
            this.items.clear();
            notifyDataSetChanged();
        }

        public void addAll(List<ServerDB> items) {
            for(ServerDB item : items) {
                this.items.add(0, item);
                notifyItemRangeInserted(0, items.size());
            }
        }
    }
}
