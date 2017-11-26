package treehou.se.habit.ui.links;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import se.treehou.ng.ohcommunicator.connector.models.OHLink;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.ui.BaseFragment;
import treehou.se.habit.ui.adapter.LinkAdapter;
import treehou.se.habit.util.ConnectionFactory;

public class LinksListFragment extends BaseFragment {

    private static final String TAG = LinksListFragment.class.getSimpleName();

    private static final String ARG_SERVER = "ARG_SERVER";

    @Inject ConnectionFactory connectionFactory;

    @BindView(R.id.list) RecyclerView listView;
    @BindView(R.id.empty) TextView emptyView;

    private LinkAdapter adapter;
    private Realm realm;
    private ServerDB server;
    private Unbinder unbinder;

    /**
     * Create fragment where user can select sitemap.
     *
     * @return Fragment
     */
    public static LinksListFragment newInstance(long serverId) {
        LinksListFragment fragment = new LinksListFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SERVER, serverId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LinksListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((HabitApplication) getActivity().getApplication()).component().inject(this);

        realm = Realm.getDefaultInstance();
        long serverId = getArguments().getLong(ARG_SERVER);
        server = ServerDB.load(realm, serverId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_links_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        setupActionBar();
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new LinkAdapter();
        adapter.setItemListener(new LinkAdapter.ItemListener() {
            @Override
            public void onItemClickListener(OHLink item) {
                openRemoveLinkDialog(item);
            }

            @Override
            public boolean onItemLongClickListener(OHLink item) {
                return false;
            }
        });
        listView.setAdapter(adapter);

        return view;
    }

    /**
     * Ask user if link should be removed.
     * @param link the link to remove.
     */
    private void openRemoveLinkDialog(OHLink link){
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.remove_link)
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> removeLink(link))
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }

    /**
     * Remove link.
     * @param link the link to remove
     */
    private void removeLink(OHLink link){
        adapter.removeItem(link);

        connectionFactory.createServerHandler(server.toGeneric(), getContext())
                .deleteLinkRx(link)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBodyResponse -> {
                    adapter.addItem(link);
                    Toast.makeText(getContext(), R.string.failed_delete_link, Toast.LENGTH_SHORT).show();
                }, throwable -> {
                    logger.e(TAG, "removeLink Failed", throwable);
                });
    }

    /**
     * Setup actionbar.
     */
    private void setupActionBar(){
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) actionBar.setTitle(R.string.links);
    }

    /**
     * Clears list of sitemaps.
     */
    private void clearList() {
        emptyView.setVisibility(View.VISIBLE);
        adapter.clear();
    }

    @Override
    public void onResume() {
        super.onResume();

        loadLinks();
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
     * Load servers from database and request their sitemaps.
     */
    private void loadLinks(){
        connectionFactory.createServerHandler(server.toGeneric(), getContext())
                .requestLinksRx()
                .filter(ohLinks -> ohLinks.size() > 0)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(ohLinks -> {
                    clearList();
                    emptyView.setVisibility(View.GONE);
                    adapter.addAll(ohLinks);
                }, throwable -> logger.w(TAG, "Failed to load link items", throwable));
    }
}
