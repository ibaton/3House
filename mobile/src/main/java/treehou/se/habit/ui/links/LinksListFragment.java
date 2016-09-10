package treehou.se.habit.ui.links;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import se.treehou.ng.ohcommunicator.connector.models.OHLink;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.module.ServerLoaderFactory;
import treehou.se.habit.ui.adapter.LinkAdapter;
import treehou.se.habit.ui.adapter.SitemapListAdapter;
import treehou.se.habit.util.ConnectionFactory;
import treehou.se.habit.util.Settings;

public class LinksListFragment extends RxFragment {

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
        listView.setAdapter(adapter);

        return view;
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
                });
    }
}
