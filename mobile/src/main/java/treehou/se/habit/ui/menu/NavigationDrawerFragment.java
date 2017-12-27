package treehou.se.habit.ui.menu;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.f2prateek.rx.preferences2.Preference;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.realm.Realm;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.R;
import treehou.se.habit.module.ApplicationComponent;
import treehou.se.habit.module.ServerLoaderFactory;
import treehou.se.habit.module.ServerLoaderFactory.ServerSitemapsResponse;
import treehou.se.habit.ui.BaseFragment;
import treehou.se.habit.util.Settings;

public class NavigationDrawerFragment extends BaseFragment {

    public static final String TAG = "NavigationDrawerFragment";

    @IntDef({NavigationItems.ITEM_SITEMAPS,
            NavigationItems.ITEM_CONTROLLERS,
            NavigationItems.ITEM_SERVER,
            NavigationItems.ITEM_SETTINGS})
    public @interface NavigationItems{
        int ITEM_SITEMAPS       = 1414;
        int ITEM_CONTROLLERS    = 1337;
        int ITEM_SERVER         = 5335;
        int ITEM_SETTINGS       = 4214;
    }

    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mUserLearnedDrawer;

    private List<DrawerItem> items = new ArrayList<>();
    private DrawerAdapter menuAdapter;

    @Inject SharedPreferences sharedPreferences;
    @Inject Settings settings;
    @Inject ServerLoaderFactory serverLoaderFactory;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getApplicationComponent().inject(this);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        mUserLearnedDrawer = sharedPreferences.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }

        items.add(new DrawerItem(getActivity().getString(R.string.sitemaps),R.drawable.menu_sitemap, NavigationItems.ITEM_SITEMAPS));
        items.add(new DrawerItem(getActivity().getString(R.string.controllers),R.drawable.menu_remote, NavigationItems.ITEM_CONTROLLERS));
        items.add(new DrawerItem(getActivity().getString(R.string.servers), R.drawable.menu_servers, NavigationItems.ITEM_SERVER));
        items.add(new DrawerItem(getActivity().getString(R.string.settings), R.drawable.menu_settings, NavigationItems.ITEM_SETTINGS));

        menuAdapter = new DrawerAdapter();
        menuAdapter.setItemClickListener(item -> selectItem(item.getValue()));
        menuAdapter.setSitemapsClickListener(this::selectSitemap);
        menuAdapter.add(items);
    }

    protected ApplicationComponent getApplicationComponent() {
        return ((HabitApplication) getActivity().getApplication()).component();
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mDrawerListView = (RecyclerView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);

        mDrawerListView.setItemAnimator(new DefaultItemAnimator());
        mDrawerListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mDrawerListView.setAdapter(menuAdapter);

        return mDrawerListView;
    }

    private Observable<List<ServerSitemapsResponse>> sitemapsObservable() {
        return Realm.getDefaultInstance().asFlowable().toObservable()
                .compose(serverLoaderFactory.loadAllServersRx())
                .compose(serverLoaderFactory.serversToSitemap(getActivity()))
                .compose(serverLoaderFactory.filterDisplaySitemapsList());
    }

    @Override
    public void onResume() {
        super.onResume();
        setupSitemapLoader();
    }

    private void setupSitemapLoader(){
        Preference<Boolean> settingsShowSitemapInMenuRx = settings.getShowSitemapsInMenuRx();
        settingsShowSitemapInMenuRx.asObservable()
                .switchMap(showSitemaps -> {
                    if(showSitemaps) {
                        return sitemapsObservable();
                    } else {
                        return Observable.just(null);
                    }
                })
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(serverSitemapsResponses -> {

                    List<OHSitemap> sitemaps = new ArrayList<>();
                    for(ServerSitemapsResponse response : serverSitemapsResponses) {
                        sitemaps.addAll(response.getSitemaps());
                    }

                    menuAdapter.clearSitemaps();
                    menuAdapter.addSitemaps(sitemaps);
                });
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its view's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the treehou.se.habit.main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),
                mDrawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    settings.userLearnedDrawer(mUserLearnedDrawer = true);
                }
                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(() -> mDrawerToggle.syncState());
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int value) {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(value);
        }
    }

    private void selectSitemap(OHSitemap sitemap) {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
        if (mCallbacks != null) {
            mCallbacks.onSitemapItemSelected(sitemap);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallbacks = (NavigationDrawerCallbacks) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mDrawerLayout != null && isDrawerOpen()) {
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity)getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int value);
        void onSitemapItemSelected(OHSitemap sitemap);
    }

}
