package treehou.se.habit.ui.servers;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.R;
import treehou.se.habit.module.ApplicationComponent;
import treehou.se.habit.ui.adapter.ImageItem;
import treehou.se.habit.ui.adapter.ImageItemAdapter;
import treehou.se.habit.ui.bindings.BindingsFragment;
import treehou.se.habit.ui.inbox.InboxListFragment;
import treehou.se.habit.ui.servers.sitemaps.SitemapSelectFragment;
import treehou.se.habit.ui.sitemaps.SitemapListFragment;

public class ServerMenuFragment extends Fragment {

    private static final String ARG_SERVER = "arg_server";

    private Unbinder unbinder;
    private long serverId;

    private ViewGroup container;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef ({
        ServerActions.ITEM_EDIT,
        ServerActions.ITEM_INBOX,
        ServerActions.ITEM_BINDINGS,
        ServerActions.ITEM_SITEMAP_FILTER
    })
    public @interface ServerActions {
        int ITEM_EDIT = 1;
        int ITEM_INBOX = 2;
        int ITEM_BINDINGS = 3;
        int ITEM_SITEMAP_FILTER = 4;
    }

    /**
     * The fragment's ListView/GridView.
     */
    @BindView(R.id.list)
    RecyclerView listView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ImageItemAdapter adapter;

    public static ServerMenuFragment newInstance(long serverId) {
        ServerMenuFragment fragment = new ServerMenuFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SERVER, serverId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ServerMenuFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        serverId = getArguments().getLong(ARG_SERVER);
        getApplicationComponent().inject(this);
    }

    protected ApplicationComponent getApplicationComponent() {
        return ((HabitApplication) getContext().getApplicationContext()).component();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.container = container;
        View view = inflater.inflate(R.layout.fragment_servers_settings, container, false);

        unbinder = ButterKnife.bind(this, view);

        ArrayList<ImageItem> items = new ArrayList<>();
        items.add(new ImageItem(ServerActions.ITEM_EDIT, getString(R.string.edit), R.drawable.ic_edit));
        items.add(new ImageItem(ServerActions.ITEM_INBOX, getString(R.string.inbox), R.drawable.ic_inbox));
        items.add(new ImageItem(ServerActions.ITEM_BINDINGS, getString(R.string.bindings), R.drawable.ic_binding));
        items.add(new ImageItem(ServerActions.ITEM_SITEMAP_FILTER, getString(R.string.sitemaps), R.drawable.ic_sitemap));
        adapter = new ImageItemAdapter(getActivity(), R.layout.item_menu_image_box);

        // Set the adapter
        listView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        listView.setLayoutManager(layoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        adapter.setItemClickListener(optionsSelectListener);
        adapter.addAll(items);
        listView.setAdapter(adapter);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) actionBar.setTitle(R.string.settings);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * Open inbox page
     * @param serverId the server to open page for.
     */
    private void openInboxPage(long serverId){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(container.getId(), InboxListFragment.newInstance(serverId))
                .addToBackStack(null)
                .commit();
    }

    /**
     * Open bindings page for server.
     * @param serverId the server to open page for.
     */
    private void openBindingsPage(long serverId){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(container.getId(), BindingsFragment.newInstance(serverId))
                .addToBackStack(null)
                .commit();
    }

    /**
     * Open sitemap page for server.
     * @param serverId the server to open page for.
     */
    private void openSitemapSettingsPage(long serverId){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(container.getId(), SitemapSelectFragment.newInstance(serverId))
                .addToBackStack(null)
                .commit();
    }

    private ImageItemAdapter.OnItemClickListener optionsSelectListener = id -> {

        Fragment fragment = null;
        switch (id) {
            case ServerActions.ITEM_EDIT:
                fragment = SetupServerFragment.newInstance(serverId);
                break;
            case ServerActions.ITEM_INBOX:
                openInboxPage(serverId);
                break;
            case ServerActions.ITEM_BINDINGS:
                openBindingsPage(serverId);
                break;
            case ServerActions.ITEM_SITEMAP_FILTER:
                openSitemapSettingsPage(serverId);
                break;
        }

        if(fragment != null){
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.page_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    };
}
