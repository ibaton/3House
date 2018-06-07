package treehou.se.habit.ui.servers.sitemaps.list

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_sitemaplist_list.*
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap
import treehou.se.habit.R
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.core.db.model.SitemapDB
import treehou.se.habit.dagger.HasActivitySubcomponentBuilders
import treehou.se.habit.dagger.ServerLoaderFactory
import treehou.se.habit.dagger.fragment.SitemapSelectComponent
import treehou.se.habit.dagger.fragment.SitemapSelectModule
import treehou.se.habit.mvp.BaseDaggerFragment
import treehou.se.habit.ui.adapter.SitemapListAdapter
import treehou.se.habit.ui.servers.sitemaps.sitemapsettings.SitemapSettingsFragment
import treehou.se.habit.util.Settings
import javax.inject.Inject

class SitemapSelectFragment : BaseDaggerFragment<SitemapSelectContract.Presenter>(), SitemapSelectContract.View {

    @Inject lateinit var settings: Settings
    @Inject lateinit var serverLoader: ServerLoaderFactory
    @Inject lateinit var sitemapPresenter: SitemapSelectContract.Presenter

    private var sitemapAdapter: SitemapListAdapter? = null
    private val serverBehaviorSubject = BehaviorSubject.create<OHServer>()
    private var serverId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        serverId = arguments!!.getLong(ARG_SHOW_SERVER)
    }

    override fun getPresenter(): SitemapSelectContract.Presenter? {
        return sitemapPresenter
    }

    override fun injectMembers(hasActivitySubcomponentBuilders: HasActivitySubcomponentBuilders) {
        (hasActivitySubcomponentBuilders.getFragmentComponentBuilder(SitemapSelectFragment::class.java) as SitemapSelectComponent.Builder)
                .fragmentModule(SitemapSelectModule(this))
                .build().injectMembers(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sitemaplist_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActionBar()

        val gridLayoutManager = GridLayoutManager(activity, 1)
        listView.layoutManager = gridLayoutManager
        listView.itemAnimator = DefaultItemAnimator()

        sitemapAdapter = SitemapListAdapter()
        sitemapAdapter!!.setSitemapSelectedListener(object : SitemapListAdapter.SitemapSelectedListener {

            override fun onSelected(server: OHServer, sitemap: OHSitemap?) {
                val sitemapDB = realm.where(SitemapDB::class.java)
                        .equalTo("server.id", serverId)
                        .equalTo("name", sitemap?.name)
                        .findFirst()

                if (sitemapDB != null) {
                    openSitemap(sitemapDB)
                }
            }

            override fun onErrorSelected(server: OHServer) {
                serverBehaviorSubject.onNext(server)
            }

            override fun onCertificateErrorSelected(server: OHServer) {}
        })
        listView.adapter = sitemapAdapter

    }

    /**
     * Open fragment showing sitemap.
     *
     * @param sitemap the name of sitemap to show.
     */
    private fun openSitemap(sitemap: SitemapDB?) {
        val fragmentManager = activity!!.supportFragmentManager
        fragmentManager.beginTransaction()
                .replace(R.id.page_container, SitemapSettingsFragment.newInstance(sitemap!!.id))
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

    /**
     * Clears list of sitemaps.
     */
    private fun clearList() {
        emptyView.visibility = View.VISIBLE
        sitemapAdapter!!.clear()

    }

    override fun onResume() {
        super.onResume()

        clearList()
        loadSitemapsFromServers()
    }

    /**
     * Load servers from database and request their sitemaps.
     */
    private fun loadSitemapsFromServers() {
        val context = context ?: return

        realm.where(ServerDB::class.java).equalTo("id", serverId).findAll().asFlowable().toObservable()
                .flatMap<ServerDB>({ Observable.fromIterable(it) })
                .map<OHServer>({ it.toGeneric() })
                .distinct()
                .compose<ServerLoaderFactory.ServerSitemapsResponse>(serverLoader.serverToSitemap(context))
                .observeOn(AndroidSchedulers.mainThread())
                .compose<ServerLoaderFactory.ServerSitemapsResponse>(bindToLifecycle<ServerLoaderFactory.ServerSitemapsResponse>())
                .subscribe({ serverSitemaps ->
                    emptyView.visibility = View.GONE

                    val server = serverSitemaps.server
                    val sitemaps = serverSitemaps.sitemaps

                    if (sitemaps != null) {
                        for (sitemap in sitemaps) {
                            if (server != null) {
                                sitemapAdapter!!.add(server, sitemap)
                            }
                        }
                    }
                }) { logger.e(TAG, "Request sitemap failed", it) }
    }

    companion object {

        private val TAG = "SitemapSelectFragment"

        private val ARG_SHOW_SERVER = "ARG_SHOW_SERVER"

        /**
         * Load sitemaps for servers.
         * Open provided sitemap if loaded.
         *
         * @param serverId the server to load
         * @return Fragment
         */
        fun newInstance(serverId: Long): SitemapSelectFragment {
            val fragment = SitemapSelectFragment()
            val args = Bundle()
            args.putLong(ARG_SHOW_SERVER, serverId)
            fragment.arguments = args
            return fragment
        }
    }
}
