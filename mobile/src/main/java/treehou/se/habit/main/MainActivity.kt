package treehou.se.habit.main

import android.os.Bundle

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuInflater

import javax.inject.Inject

import de.duenndns.ssl.MemorizingTrustManager
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap
import treehou.se.habit.mvp.BaseDaggerActivity
import treehou.se.habit.R
import treehou.se.habit.module.HasActivitySubcomponentBuilders
import treehou.se.habit.module.ServerLoaderFactory
import treehou.se.habit.ui.control.ControllerUtil
import treehou.se.habit.ui.control.ControllsFragment
import treehou.se.habit.ui.menu.NavigationDrawerFragment
import treehou.se.habit.ui.settings.SettingsFragment
import treehou.se.habit.ui.servers.serverlist.ServersFragment
import treehou.se.habit.ui.sitemaps.sitemap.SitemapFragment
import treehou.se.habit.ui.sitemaps.sitemaplist.SitemapListFragment
import treehou.se.habit.util.ConnectionFactory
import treehou.se.habit.util.Settings

import treehou.se.habit.ui.menu.NavigationDrawerFragment.NavigationItems


class MainActivity : BaseDaggerActivity<MainContract.Presenter>(), NavigationDrawerFragment.NavigationDrawerCallbacks, MainContract.View {

    @Inject lateinit var mainPresenter: MainPresenter
    @Inject lateinit var connectionFactory: ConnectionFactory
    @Inject lateinit var serverLoaderFactory: ServerLoaderFactory
    @Inject lateinit var controllerUtil: ControllerUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(settings.themeResourse)

        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

        // Fragment managing the behaviors, interactions and presentation of the navigation drawer.
        val navigationDrawerFragment = supportFragmentManager.findFragmentById(R.id.navigation_drawer) as NavigationDrawerFragment

        // Set up the drawer.
        navigationDrawerFragment.setUp(R.id.navigation_drawer, findViewById(R.id.drawer_layout) as DrawerLayout)
    }

    override fun getPresenter(): MainContract.Presenter? {
        return mainPresenter
    }

    override fun injectMembers(hasActivitySubcomponentBuilders: HasActivitySubcomponentBuilders) {
        (hasActivitySubcomponentBuilders.getActivityComponentBuilder(MainActivity::class.java) as MainActivityComponent.Builder)
                .activityModule(MainActivityModule(this))
                .build().injectMembers(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu items for use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSitemapItemSelected(sitemap: OHSitemap) {
        mainPresenter.showSitemap(sitemap)
    }

    override fun onNavigationDrawerItemSelected(@NavigationDrawerFragment.NavigationItems value: Int) {
        when (value) {
            NavigationItems.ITEM_SITEMAPS -> mainPresenter.showSitemaps()
            NavigationItems.ITEM_CONTROLLERS -> mainPresenter.showControllers()
            NavigationItems.ITEM_SERVER -> mainPresenter.showServers()
            NavigationItems.ITEM_SETTINGS -> mainPresenter.showSettings()
        }
    }

    fun openFragment(fragment: Fragment?) {
        clearFragments()
        if (fragment != null) {
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction()
                    .replace(R.id.page_container, fragment)
                    .commit()
        }
    }

    override fun openSitemaps() {
        val fragment = SitemapListFragment.newInstance()
        openFragment(fragment)
    }

    override fun openSitemaps(defaultSitemap: String) {
        val fragment = SitemapListFragment.newInstance(defaultSitemap)
        openFragment(fragment)
    }

    override fun openSitemap(sitemap: OHSitemap) {
        val fragment = SitemapListFragment.newInstance(sitemap.name)
        openFragment(fragment)
    }

    override fun openControllers() {
        val fragment = ControllsFragment.newInstance()
        openFragment(fragment)
    }

    override fun openServers() {
        val fragment = ServersFragment.newInstance()
        openFragment(fragment)
    }

    override fun openSettings() {
        val fragment = SettingsFragment.newInstance()
        openFragment(fragment)
    }

    override fun onBackPressed() {
        val mDrawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START)
            return
        }

        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentById(R.id.page_container)
        if (fragment is SitemapFragment) {
            val result = fragment.removeAllPages()
            if (result) {
                return
            }
        }
        super.onBackPressed()
    }

    override fun hasOpenPage(): Boolean {
        return supportFragmentManager.findFragmentById(R.id.page_container) != null
    }

    /**
     * Clear fragments on backstack.
     */
    private fun clearFragments() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStackImmediate()
        }
    }

    companion object {

        private val TAG = "MainActivity"
    }
}
