package treehou.se.habit.ui.inbox;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.connector.models.OHInboxItem;
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback;
import se.treehou.ng.ohcommunicator.services.callbacks.OHResponse;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.ui.adapter.InboxAdapter;

public class InboxListFragment extends RxFragment {

    private static final String TAG = "InboxListFragment";

    private static final String ARG_SERVER = "argServer";

    @Bind(R.id.list) RecyclerView listView;

    private Realm relam;

    private ServerDB server;
    private InboxAdapter adapter;

    private List<OHInboxItem> items = new ArrayList<>();
    private OHCallback<List<OHInboxItem>> inboxCallback;

    private boolean showIgnored = false;
    private MenuItem actionHide;
    private MenuItem actionShow;

    /**
     * Creates a new instance of inbox list fragment.
     *
     * @param server the server to connect to.
     * @return new fragment instance.
     */
    public static InboxListFragment newInstance(ServerDB server) {
        InboxListFragment fragment = new InboxListFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SERVER, server.getId());
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

        relam = Realm.getDefaultInstance();
        server = ServerDB.load(relam, getArguments().getLong(ARG_SERVER));

        inboxCallback = new OHCallback<List<OHInboxItem>>() {
            @Override
            public void onUpdate(OHResponse<List<OHInboxItem>> response) {
                items = response.body();
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
        ButterKnife.bind(this, view);

        setupActionbar();
        hookupInboxList();
        setHasOptionsMenu(true);

        return view;
    }

    /**
     * Setup inbox list.
     */
    private void hookupInboxList(){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        listView.setLayoutManager(gridLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof InboxAdapter.IgnoreInboxHolder) return ItemTouchHelper.LEFT;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if(ItemTouchHelper.RIGHT == swipeDir){
                    OHInboxItem item = adapter.getItem(viewHolder.getAdapterPosition());
                    ignoreInboxItem(item);
                }
                if(ItemTouchHelper.LEFT == swipeDir){
                    OHInboxItem item = adapter.getItem(viewHolder.getAdapterPosition());
                    unignoreInboxItem(item);
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
    }

    /**
     * Set up actionbar
     */
    private void setupActionbar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(R.string.inbox);
        }
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

    /**
     * Set if ignored items should be shown.
     * @param showIgnored true to show ignored items, else false.
     */
    private void showIgnoredItems(boolean showIgnored){

        this.showIgnored = showIgnored;
        setItems(items, showIgnored);
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
        List<OHInboxItem> inboxItems = new ArrayList<>(items);
        Log.d(TAG, "Received items " + inboxItems);

        Collections.sort(inboxItems, new InboxItemComparator());

        adapter.clear();
        if (!showIgnored) {
            for (Iterator<OHInboxItem> it = inboxItems.iterator(); it.hasNext();) {
                if (it.next().isIgnored()) {
                    it.remove();
                }
            }
        }
        adapter.addAll(inboxItems);
    }

    /**
     * Ignore inbox item.
     * Removes the inbox item from the list.
     * Sends ignore request to the server.
     *
     * @param item the item to hide.
     */
    private void ignoreInboxItem(final OHInboxItem item){
        item.setFlag(OHInboxItem.FLAG_IGNORED);
        Openhab.instance(server.toGeneric()).ignoreInboxItem(item);

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
        setItems(items, showIgnored);
    }

    /**
     * Unignore inbox item.
     * Removes the inbox item from the list.
     * Sends unignore request to the server.
     *
     * @param item the item to hide.
     */
    private void unignoreInboxItem(final OHInboxItem item) {
        item.setFlag("");
        Openhab.instance(server.toGeneric()).unignoreInboxItem(item);
        setItems(items, showIgnored);
    }

    @Override
    public void onResume() {
        super.onResume();

        Openhab.instance(server.toGeneric()).requestInboxItems(inboxCallback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        relam.close();
    }

    /**
     * Comparator for sorting inbox item in list.
     */
    private static class InboxItemComparator implements Comparator<OHInboxItem> {
        @Override
        public int compare(OHInboxItem lhs, OHInboxItem rhs) {

            int inboxCompare = lhs.getLabel().compareTo(rhs.getLabel());
            if(inboxCompare == 0){
                return lhs.getThingUID().compareTo(rhs.getThingUID());
            }

            return inboxCompare;
        }
    }
}
