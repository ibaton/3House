package treehou.se.habit.ui.sitemaps.sitemaplist

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_sitemaplist_list.*
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap
import treehou.se.habit.R
import treehou.se.habit.dagger.HasActivitySubcomponentBuilders
import treehou.se.habit.dagger.fragment.SitemapListComponent
import treehou.se.habit.dagger.fragment.SitemapListModule
import treehou.se.habit.mvp.BaseDaggerFragment
import treehou.se.habit.ui.adapter.SitemapListAdapter
import treehou.se.habit.ui.adapter.SitemapListAdapter.ServerState
import treehou.se.habit.ui.sitemaps.sitemap.SitemapFragment
import javax.inject.Inject
import javax.net.ssl.SSLPeerUnverifiedException

/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class SitemapListFragment : BaseDaggerFragment<SitemapListContract.Presenter>(), SitemapListContract.View {

    @Inject lateinit var sitemapListPresenter: SitemapListContract.Presenter

    private var sitemapAdapter: SitemapListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sitemaplist_list, container, false)
    }

    private fun setupSitemapListAdapter() {
        val gridLayoutManager = GridLayoutManager(activity, 1)
        listView.layoutManager = gridLayoutManager
        listView.itemAnimator = DefaultItemAnimator()

        sitemapAdapter = SitemapListAdapter()
        sitemapAdapter!!.setSitemapSelectedListener(object : SitemapListAdapter.SitemapSelectedListener {

            override fun onSelected(server: OHServer, sitemap: OHSitemap?) {
                sitemapListPresenter.openSitemap(server, sitemap)
            }

            override fun onErrorSelected(server: OHServer) {
                Log.d(TAG, "Reloading server: " + server.displayName)
                sitemapListPresenter.reloadSitemaps(server)
            }

            override fun onCertificateErrorSelected(server: OHServer) {

            }
        })
        listView.adapter = sitemapAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActionBar()
        setupSitemapListAdapter()
    }

    /**
     * Open fragment showing sitemap.
     *
     * @param server the server of default sitemap.
     * @param sitemap the name of sitemap to show.
     */
    override fun showSitemap(server: OHServer, sitemap: OHSitemap) {
        val fragmentManager = activity!!.supportFragmentManager
        fragmentManager.beginTransaction()
                .replace(R.id.page_container, SitemapFragment.newInstance(server, sitemap))
                .addToBackStack(null)
                .commit()
    }

    /**
     * Setup actionbar.
     */
    private fun setupActionBar() {
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setTitle(R.string.sitemaps)
    }

    override fun hideEmptyView() {
        emptyView.visibility = View.GONE
    }

    /**
     * Clears list of sitemaps.
     */
    override fun clearList() {
        emptyView.visibility = View.VISIBLE
        sitemapAdapter!!.clear()
    }

    override fun showServerError(server: OHServer, error: Throwable) {
        if (error is SSLPeerUnverifiedException) {
            sitemapAdapter!!.setServerState(server, ServerState.STATE_CERTIFICATE_ERROR)
        } else {
            sitemapAdapter!!.setServerState(server, ServerState.STATE_ERROR)
        }
    }

    override fun populateSitemaps(server: OHServer, sitemaps: List<OHSitemap>) {
        for (sitemap in sitemaps) {
            sitemapAdapter!!.add(server, sitemap)
        }
    }

    override fun getPresenter(): SitemapListContract.Presenter? {
        return sitemapListPresenter
    }

    override fun injectMembers(hasActivitySubcomponentBuilders: HasActivitySubcomponentBuilders) {
        (hasActivitySubcomponentBuilders.getFragmentComponentBuilder(SitemapListFragment::class.java) as SitemapListComponent.Builder)
                .fragmentModule(SitemapListModule(this, arguments!!))
                .build().injectMembers(this)
    }

    companion object {

        private val TAG = "SitemapSelectFragment"

        val ARG_SHOW_SITEMAP = "showSitemap"

        /**
         * Create fragment where user can select sitemap.
         *
         * @return Fragment
         */
        fun newInstance(): SitemapListFragment {
            val fragment = SitemapListFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

        /**
         * Load sitemaps for servers.
         * Open provided sitemap if loaded.
         *
         * @param sitemap name of sitemap to load
         * @return Fragment
         */
        fun newInstance(sitemap: String): SitemapListFragment {
            val fragment = SitemapListFragment()
            val args = Bundle()
            args.putString(ARG_SHOW_SITEMAP, sitemap)
            fragment.arguments = args
            return fragment
        }
    }
}
