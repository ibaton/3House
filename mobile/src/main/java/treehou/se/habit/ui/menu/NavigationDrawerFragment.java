package treehou.se.habit.ui.menu;


import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.f2prateek.rx.preferences.Preference;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import rx.android.schedulers.AndroidSchedulers;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.R;
import treehou.se.habit.module.ApplicationComponent;
import treehou.se.habit.module.ServerLoaderFactory;
import treehou.se.habit.util.Settings;

public class NavigationDrawerFragment extends RxFragment {

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
    private List<OHSitemap> sitemaps = new ArrayList<>();
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

    /**
     * Load servers from database and request their sitemaps.
     */
    private void loadSitemapsFromServers(){
        sitemaps.clear();
        menuAdapter.clearSitemaps();

        Realm.getDefaultInstance().asObservable().compose(serverLoaderFactory.loadServersRx())
                .compose(serverLoaderFactory.serverToSitemap(getActivity()))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycle.bindFragment(this.lifecycle()))
                .compose(serverLoaderFactory.filterDisplaySitemaps())
                .subscribe(serverSitemaps -> {
                    List<OHSitemap> sitemaps = serverSitemaps.second;

                    boolean autoloadLast = settings.getAutoloadSitemapRx().get();
                    this.sitemaps.addAll(sitemaps);

                    menuAdapter.addSitemaps(sitemaps);
                });
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

    @Override
    public void onResume() {
        super.onResume();

        Preference<Boolean> settingsShowSitemapInMenuRx = settings.getShowSitemapsInMenuRx();
        settingsShowSitemapInMenuRx.asObservable()
                .compose(bindToLifecycle())
                .subscribe(showSitemaps -> {
                    if(showSitemaps){
                        loadSitemapsFromServers();
                    }else {
                        sitemaps.clear();
                        menuAdapter.clearSitemaps();
                    }
                });
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
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

    public static class DrawerItem{

        private String name;
        private int resource;
        @NavigationItems int value;

        public DrawerItem(String name, int resource, int value) {
            this.name = name;
            this.value = value;
            this.resource = resource;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public @NavigationItems int getValue() {
            return value;
        }

        public void setValue(@NavigationItems int value) {
            this.value = value;
        }

        public int getResource() {
            return resource;
        }

        public void setResource(int resource) {
            this.resource = resource;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    static class DrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int VIEW_TYPE_ITEM = 1;
        private static final int VIEW_TYPE_SITEMAP = 2;

        private List<DrawerItem> items = new ArrayList<>();
        private List<OHSitemap> sitemaps = new ArrayList<>();
        private OnItemClickListener itemClickListener;
        private OnSitemapClickListener sitemapItemClickListener;

        interface  OnItemClickListener{
            void onClickItem(DrawerItem item);
        }

        interface OnSitemapClickListener {
            void onClickItem(OHSitemap item);
        }

        static class DrawerItemHolder extends RecyclerView.ViewHolder {

            private ImageView imgIcon;
            private TextView lblName;

            public DrawerItemHolder(View itemView) {
                super(itemView);
                imgIcon = (ImageView) itemView.findViewById(R.id.img_icon);
                lblName = (TextView) itemView.findViewById(R.id.lbl_name);
            }

            public void update(DrawerItem entry){
                lblName.setText(entry.getName());
                if(entry.getResource() != 0) {
                    imgIcon.setImageResource(entry.getResource());
                }
            }
        }

        static class SitemapItemHolder extends RecyclerView.ViewHolder {

            private TextView lblName;

            public SitemapItemHolder(View itemView) {
                super(itemView);
                lblName = (TextView) itemView.findViewById(R.id.lbl_sitemap);
            }

            public void update(OHSitemap entry){
                lblName.setText(entry.getName());
            }
        }

        public DrawerAdapter() {
        }

        public void setItemClickListener(OnItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public void setSitemapsClickListener(OnSitemapClickListener itemClickListener) {
            sitemapItemClickListener = itemClickListener;
        }

        /**
         * Add menu items to add.
         * @param drawerItems the items to add.
         */
        public void add(List<DrawerItem> drawerItems){
            items.addAll(drawerItems);
            notifyItemRangeInserted(0, drawerItems.size());
        }

        /**
         * Add sitemaps that should be shown in menu.
         *
         * @param sitemaps the sitemaps that should be shown in menu.
         */
        public void addSitemaps(List<OHSitemap> sitemaps){
            this.sitemaps.addAll(sitemaps);
            notifyItemRangeInserted(findPosition(NavigationItems.ITEM_SITEMAPS), sitemaps.size());
        }

        /**
         * Remove all sitemaps added to menu.
         */
        public void clearSitemaps(){
            int sitemapSize = sitemaps.size();
            notifyItemRangeRemoved(findPosition(NavigationItems.ITEM_SITEMAPS), sitemapSize);
            sitemaps.clear();
        }

        /**
         * Get sitemap from position.
         * @param position the menu position to get sitemap for.
         * @return sitemap at position.
         */
        private OHSitemap getSitemap(int position){
            return sitemaps.get(position-(findPosition(NavigationItems.ITEM_SITEMAPS)+1));
        }

        /**
         * Get sitemap from position.
         * @param position the menu position to get sitemap for.
         * @return sitemap at position.
         */
        private DrawerItem getMenuItem(int position){
            int mainItemPosition = 0;
            for(int i=0; i<getItemCount(); i++){
                int itemViewType = getItemViewType(i);

                if(VIEW_TYPE_ITEM == itemViewType){
                    DrawerItem item = items.get(mainItemPosition);
                    if(position == i) {
                        return item;
                    }
                    mainItemPosition++;
                }
            }
            return null;
        }

        /**
         * Get menu item position.
         *
         * @param navigationItem the navigation item to search for.
         * @return navigation item position.
         */
        private int findPosition(@NavigationItems int navigationItem){
            int mainItemPosition = 0;
            for(int i=0; i<getItemCount(); i++){
                int itemViewType = getItemViewType(i);

                if(VIEW_TYPE_ITEM == itemViewType){
                    DrawerItem item = items.get(mainItemPosition);
                    if(item.getValue() == navigationItem){
                        return i;
                    }
                    mainItemPosition++;
                }
            }
            return -1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            RecyclerView.ViewHolder drawerItemHolder;
            if(VIEW_TYPE_SITEMAP == viewType){
                View itemView = inflater.inflate(R.layout.item_sitemap_small, parent, false);
                drawerItemHolder = new SitemapItemHolder(itemView);
            }
            else {
                View itemView = inflater.inflate(R.layout.item_drawer, parent, false);
                drawerItemHolder = new DrawerItemHolder(itemView);
            }

            return drawerItemHolder;
        }



        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int itemType = getItemViewType(position);

            if(VIEW_TYPE_SITEMAP == itemType){
                OHSitemap sitemap = getSitemap(position);
                SitemapItemHolder sitemapItemHolder = (SitemapItemHolder) holder;
                sitemapItemHolder.update(sitemap);
                sitemapItemHolder.itemView.setOnClickListener(view -> {
                    if (sitemapItemClickListener != null) {
                        sitemapItemClickListener.onClickItem(sitemap);
                    }
                });
            }
            else {
                DrawerItem drawerItem = getMenuItem(position);
                DrawerItemHolder drawerItemHolder = (DrawerItemHolder) holder;
                drawerItemHolder.update(drawerItem);
                drawerItemHolder.itemView.setOnClickListener(view -> {
                    if (itemClickListener != null) {
                        itemClickListener.onClickItem(drawerItem);
                    }
                });
            }
        }

        @Override
        public int getItemViewType(int position) {

            int menuPosition = 0;
            for(int i=0; i<=position; i++){
                DrawerItem menuItem = items.get(menuPosition);
                if(i == position){
                    return VIEW_TYPE_ITEM;
                }

                if(menuItem.getValue() == NavigationItems.ITEM_SITEMAPS){
                    for(int sitemapPosition=0; sitemapPosition<sitemaps.size(); sitemapPosition++){
                        i++;
                        if(i == position){
                            return VIEW_TYPE_SITEMAP;
                        }
                    }
                }
                menuPosition++;
            }

            return -1;
        }

        @Override
        public int getItemCount() {
            return items.size() + sitemaps.size();
        }
    }
}
