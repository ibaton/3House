package treehou.se.habit.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.core.OHInboxItem;
import se.treehou.ng.ohcommunicator.core.OHServer;
import se.treehou.ng.ohcommunicator.services.callbacks.Callback1;
import treehou.se.habit.R;
import treehou.se.habit.connector.GsonHelper;

public class InboxListFragment extends Fragment {

    private static final String TAG = "InboxListFragment";

    private static final String ARG_SERVER = "argServer";

    private OHServer server;
    private InboxAdapter adapter;
    private Callback1<List<OHInboxItem>> inboxCallback;

    private boolean showIgnored = false;
    private MenuItem actionHide;
    private MenuItem actionShow;

    public static InboxListFragment newInstance(OHServer server) {
        InboxListFragment fragment = new InboxListFragment();
        Bundle args = new Bundle();
        String jServer = GsonHelper.createGsonBuilder().toJson(server);
        args.putString(ARG_SERVER, jServer);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public InboxListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        server = GsonHelper.createGsonBuilder().fromJson(getArguments().getString(ARG_SERVER), OHServer.class);
        inboxCallback = new Callback1<List<OHInboxItem>>() {
            @Override
            public void onUpdate(List<OHInboxItem> items) {
                setItems(items, showIgnored);
            }

            @Override
            public void onError() {}
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(R.string.inbox);
        }

        RecyclerView listView = (RecyclerView) view.findViewById(R.id.list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        listView.setLayoutManager(gridLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if(ItemTouchHelper.RIGHT == swipeDir){
                    OHInboxItem item = adapter.getItem(viewHolder.getAdapterPosition());
                    ignoreInboxItem(item);
                }
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(listView);

        adapter = new InboxAdapter(getContext(), server);
        listView.setAdapter(adapter);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.inbox, menu);
        actionHide = menu.findItem(R.id.action_hide);
        actionHide.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showIgnoredItems(false);
                return true;
            }
        });

        actionShow = menu.findItem(R.id.action_show);
        actionShow.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showIgnoredItems(true);
                return true;
            }
        });

        updateIgnoreButtons(showIgnored);
    }

    private void showIgnoredItems(boolean showIgnored){

        this.showIgnored = showIgnored;
        setItems(Openhab.instance(server).getInboxItems(), showIgnored);
        updateIgnoreButtons(showIgnored);

        final View rootView = getView();
        if(rootView != null) {
            Snackbar.make(rootView, showIgnored ? getString(R.string.show_ignored) : getString(R.string.hide_ignored), Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Update icons for showing if viewing ignored items or not.
     *
     * @param showIgnored True to show ignored ignored items else false.
     */
    private void updateIgnoreButtons(boolean showIgnored){
        actionShow.setVisible(!showIgnored);
        actionHide.setVisible(showIgnored);
    }

    /**
     * Set all the items that should be displayed in list.
     * Clears and updates adapter accordingly.
     *
     * @param items the items to show.
     * @param showIgnored true to filter out ignored items.
     */
    private void setItems(List<OHInboxItem> items, boolean showIgnored){
        adapter.clear();
        if (!showIgnored) {
            for (Iterator<OHInboxItem> it = items.iterator(); it.hasNext();) {
                if (it.next().isIgnored()) {
                    it.remove();
                }
            }
        }
        adapter.addAll(items);
    }

    /**
     * Ignore inbox item.
     * Removes the inbox item from the list.
     * Sends ignore request to the server.
     *
     * @param item the item to hide.
     */
    private void ignoreInboxItem(final OHInboxItem item){
        adapter.removeItem(item);
        Openhab.instance(server).ignoreInboxItem(item);

        final View rootView = getView();
        if(rootView != null) {
            Snackbar.make(rootView, R.string.hide_item, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            unignoreInboxItem(item);
                            Snackbar.make(rootView, R.string.restore_item, Snackbar.LENGTH_SHORT).show();
                        }
                    }).show();
        }
    }

    /**
     * Unignore inbox item.
     * Removes the inbox item from the list.
     * Sends unignore request to the server.
     *
     * @param item the item to hide.
     */
    private void unignoreInboxItem(final OHInboxItem item) {
        Openhab.instance(server).unignoreInboxItem(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        Openhab.instance(server).registerInboxListener(inboxCallback);
    }

    @Override
    public void onPause() {
        super.onPause();

        Openhab.instance(server).deregisterInboxListener(inboxCallback);
    }

    public static class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.InboxHolder>{

        private List<OHInboxItem> items = new ArrayList<>();
        private Context context;

        private OHServer server;
        private ItemListener itemListener = new DummyItemListener();

        public class InboxHolder extends RecyclerView.ViewHolder {
            public final TextView lblName;
            public LinearLayout louProperties;

            public InboxHolder(View view) {
                super(view);
                lblName = (TextView) view.findViewById(R.id.lbl_server);
                louProperties = (LinearLayout) itemView.findViewById(R.id.lou_properties);
            }
        }

        public InboxAdapter(Context context, OHServer server) {
            this.context = context;
            this.server = server;
        }

        @Override
        public InboxHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View itemView = inflater.inflate(R.layout.item_inbox, null);

            return new InboxHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final InboxHolder serverHolder, final int position) {
            final OHInboxItem inboxItem = items.get(position);

            serverHolder.lblName.setText(inboxItem.getLabel());
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
            });

            LinearLayout louProperties = serverHolder.louProperties;
            louProperties.removeAllViews();

            LayoutInflater inflater = LayoutInflater.from(context);
            for(Map.Entry<String, String> entry : inboxItem.getProperties().entrySet()){
                View louProperty = inflater.inflate(R.layout.item_property, louProperties, false);
                TextView lblProperty = (TextView) louProperty.findViewById(R.id.lbl_property);
                lblProperty.setText(context.getString(R.string.inbox_property, entry.getKey(), entry.getValue()));

                louProperties.addView(louProperty);
            }

            serverHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.approve_item))
                            .setMessage(context.getString(R.string.approve_this_item))
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Openhab.instance(server).approveInboxItem(inboxItem);
                                }
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public OHInboxItem getItem(int position) {
            return items.get(position);
        }

        interface ItemListener{

            void onItemClickListener(InboxHolder serverHolder);

            boolean onItemLongClickListener(InboxHolder serverHolder);

            void itemCountUpdated(int itemCount);
        }

        public class DummyItemListener implements ItemListener {

            @Override
            public void onItemClickListener(InboxHolder serverHolder) {}

            @Override
            public boolean onItemLongClickListener(InboxHolder serverHolder) {
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

        public void addItem(OHInboxItem item) {
            items.add(0, item);
            notifyItemInserted(0);
            itemListener.itemCountUpdated(items.size());
        }

        public void addAll(List<OHInboxItem> items) {
            for(OHInboxItem item : items) {
                this.items.add(0, item);
                notifyItemRangeInserted(0, items.size());
            }
            itemListener.itemCountUpdated(items.size());
        }

        public void removeItem(int position) {
            Log.d(TAG, "removeItem: " + position);
            items.remove(position);
            notifyItemRemoved(position);
            itemListener.itemCountUpdated(items.size());
        }

        public void removeItem(OHInboxItem item) {
            int position = items.indexOf(item);
            items.remove(position);
            notifyItemRemoved(position);
            itemListener.itemCountUpdated(items.size());
        }

        public void clear() {
            this.items.clear();
            notifyDataSetChanged();
            itemListener.itemCountUpdated(items.size());
        }
    }
}
