package treehou.se.habit.ui.menu


import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.support.annotation.IntDef
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import java.util.ArrayList

import javax.inject.Inject

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.realm.Realm
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap
import treehou.se.habit.HabitApplication
import treehou.se.habit.R
import treehou.se.habit.dagger.ApplicationComponent
import treehou.se.habit.dagger.ServerLoaderFactory
import treehou.se.habit.dagger.ServerLoaderFactory.ServerSitemapsResponse
import treehou.se.habit.ui.BaseFragment
import treehou.se.habit.util.Settings

class NavigationDrawerFragment : BaseFragment() {

    private var mCallbacks: NavigationDrawerCallbacks = DummyNavigationDrawerCallbacks()

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private var mDrawerToggle: ActionBarDrawerToggle? = null

    private var mDrawerLayout: DrawerLayout? = null
    private var mDrawerListView: RecyclerView? = null
    private var mFragmentContainerView: View? = null

    private var mCurrentSelectedPosition = 0
    private var mUserLearnedDrawer: Boolean = false

    private val items = ArrayList<DrawerItem>()
    private lateinit var menuAdapter: DrawerAdapter

    @Inject lateinit var sharedPreferences: SharedPreferences
    @Inject lateinit var settings: Settings
    @Inject lateinit var serverLoaderFactory: ServerLoaderFactory

    protected val applicationComponent: ApplicationComponent
        get() = (activity!!.application as HabitApplication).component()

    val isDrawerOpen: Boolean
        get() = mDrawerLayout != null && mDrawerLayout!!.isDrawerOpen(mFragmentContainerView!!)

    private val actionBar: ActionBar?
        get() = (activity as AppCompatActivity).supportActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        applicationComponent.inject(this)

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        mUserLearnedDrawer = sharedPreferences.getBoolean(PREF_USER_LEARNED_DRAWER, false)

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION)
        }

        items.add(DrawerItem(activity!!.getString(R.string.sitemaps), R.drawable.menu_sitemap, ITEM_SITEMAPS))
        items.add(DrawerItem(activity!!.getString(R.string.controllers), R.drawable.menu_remote, ITEM_CONTROLLERS))
        items.add(DrawerItem(activity!!.getString(R.string.servers), R.drawable.menu_servers, ITEM_SERVER))
        items.add(DrawerItem(activity!!.getString(R.string.settings), R.drawable.menu_settings, ITEM_SETTINGS))

        menuAdapter = DrawerAdapter()

        val itemClickListener = object : DrawerAdapter.OnItemClickListener {
            override fun onClickItem(item: DrawerItem) {
                selectItem(item.value)
            }
        }

        val sitemapClickListener = object : DrawerAdapter.OnSitemapClickListener {
            override fun onClickItem(item: OHSitemap) {
                selectSitemap(item)
            }
        }

        menuAdapter.setItemClickListener(itemClickListener)
        menuAdapter.setSitemapsClickListener(sitemapClickListener)
        menuAdapter.add(items)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mDrawerListView = inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false) as RecyclerView

        mDrawerListView!!.itemAnimator = DefaultItemAnimator()
        mDrawerListView!!.layoutManager = LinearLayoutManager(context)
        mDrawerListView!!.adapter = menuAdapter

        return mDrawerListView
    }

    private fun sitemapsObservable(): Observable<List<ServerSitemapsResponse>> {
        return Realm.getDefaultInstance().asFlowable().toObservable()
                .compose<List<OHServer>>(serverLoaderFactory.loadAllServersRx())
                .compose(serverLoaderFactory.serversToSitemap(activity))
                .compose(serverLoaderFactory.filterDisplaySitemapsList())
    }

    override fun onResume() {
        super.onResume()
        setupSitemapLoader()
    }

    private fun setupSitemapLoader() {
        val settingsShowSitemapInMenuRx = settings.showSitemapsInMenuRx
        settingsShowSitemapInMenuRx.asObservable()
                .switchMap<List<ServerSitemapsResponse>> { showSitemaps ->
                    if (showSitemaps) {
                        sitemapsObservable()
                    } else {
                        Observable.just(listOf())
                    }
                }
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ serverSitemapsResponses ->

                    val sitemaps = ArrayList<OHSitemap>()
                    for (response in serverSitemapsResponses) {
                        val newSitemaps = response.sitemaps
                        if(newSitemaps != null) {
                            sitemaps.addAll(newSitemaps)
                        }
                    }

                    menuAdapter.clearSitemaps()
                    menuAdapter.addSitemaps(sitemaps)
                }, { logger.e(TAG, "Failed to setupSitemapLoader", it) })
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its view's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    fun setUp(fragmentId: Int, drawerLayout: DrawerLayout) {
        mFragmentContainerView = activity!!.findViewById(fragmentId)
        mDrawerLayout = drawerLayout

        // set a custom shadow that overlays the treehou.se.habit.ui.main content when the drawer opens
        mDrawerLayout!!.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START)
        // set up the drawer's list view with items and click listener

        val actionBar = actionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeButtonEnabled(true)

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = object : ActionBarDrawerToggle(
                activity,
                mDrawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                if (!isAdded) {
                    return
                }

                activity!!.invalidateOptionsMenu()
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                if (!isAdded) {
                    return
                }

                if (!mUserLearnedDrawer) {
                    settings.userLearnedDrawer(true)
                }
                activity!!.invalidateOptionsMenu()
            }
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout!!.post { mDrawerToggle!!.syncState() }
        mDrawerLayout!!.setDrawerListener(mDrawerToggle)
    }

    private fun selectItem(value: Int) {
        mDrawerLayout!!.closeDrawer(mFragmentContainerView!!)
        mCallbacks.onNavigationDrawerItemSelected(value)
    }

    private fun selectSitemap(sitemap: OHSitemap) {
        mDrawerLayout!!.closeDrawer(mFragmentContainerView!!)
        mCallbacks.onSitemapItemSelected(sitemap)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mCallbacks = if (context is NavigationDrawerCallbacks) context else DummyNavigationDrawerCallbacks()
    }

    override fun onDetach() {
        super.onDetach()
        mCallbacks = DummyNavigationDrawerCallbacks()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle!!.onConfigurationChanged(newConfig)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        if (mDrawerLayout != null && isDrawerOpen) {
            showGlobalContextActionBar()
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return mDrawerToggle!!.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private fun showGlobalContextActionBar() {
        val actionBar = actionBar
        actionBar!!.setDisplayShowTitleEnabled(true)
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        fun onNavigationDrawerItemSelected(value: Int)

        fun onSitemapItemSelected(sitemap: OHSitemap)
    }

    class DummyNavigationDrawerCallbacks: NavigationDrawerCallbacks{
        override fun onSitemapItemSelected(sitemap: OHSitemap) {}

        override fun onNavigationDrawerItemSelected(value: Int) {}
    }

    companion object {

        val TAG = "NavigationDrawerFragment"

        private val STATE_SELECTED_POSITION = "selected_navigation_drawer_position"
        private val PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned"

        @IntDef(ITEM_SITEMAPS, ITEM_CONTROLLERS, ITEM_SERVER, ITEM_SETTINGS)
        @Retention(AnnotationRetention.SOURCE)
        annotation class NavigationItems

        const val ITEM_SITEMAPS = 1414
        const val ITEM_CONTROLLERS = 1337
        const val ITEM_SERVER = 5335
        const val ITEM_SETTINGS = 4214
    }

}
