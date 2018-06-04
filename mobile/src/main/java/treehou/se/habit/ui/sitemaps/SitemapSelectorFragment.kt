package treehou.se.habit.ui.sitemaps

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import javax.inject.Inject

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_sitemap_selector.*
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap
import treehou.se.habit.R
import treehou.se.habit.ui.BaseFragment
import treehou.se.habit.ui.adapter.SitemapAdapter
import treehou.se.habit.util.ConnectionFactory
import treehou.se.habit.util.Util

class SitemapSelectorFragment : BaseFragment() {

    @Inject lateinit var connectionFactory: ConnectionFactory

    private var sitemapAdapter: SitemapAdapter? = null

    private val sitemapSelectListener = object : SitemapAdapter.OnSitemapSelectListener {
        override fun onSitemapSelect(sitemap: OHSitemap) {
            if (targetFragment != null) {
                (targetFragment as SitemapAdapter.OnSitemapSelectListener).onSitemapSelect(sitemap)
            } else {
                (activity as SitemapAdapter.OnSitemapSelectListener).onSitemapSelect(sitemap)
            }
        }

        override fun onErrorClicked(server: OHServer) {
            requestSitemap(server)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Util.getApplicationComponent(this).inject(this)

        sitemapAdapter = SitemapAdapter()
        sitemapAdapter!!.setSelectorListener(sitemapSelectListener)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sitemap_selector, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gridLayoutManager = GridLayoutManager(activity, 1)

        list.layoutManager = gridLayoutManager
        list.itemAnimator = DefaultItemAnimator()
        list.adapter = sitemapAdapter
    }

    override fun onResume() {
        super.onResume()

        sitemapAdapter!!.clear()
    }

    /**
     * Request sitemaps for server.
     * @param server the server to request sitemap for.
     */
    private fun requestSitemap(server: OHServer) {
        sitemapAdapter!!.setServerState(server, SitemapAdapter.SitemapItem.STATE_LOADING)
        val serverHandler = connectionFactory.createServerHandler(server, context)

        serverHandler.requestSitemapRx()
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ sitemaps ->
                    for (sitemap in sitemaps) {
                        sitemap.server = server
                        if (!sitemapAdapter!!.contains(sitemap)) {
                            sitemapAdapter!!.add(sitemap)
                        } else if (OHSitemap.isLocal(sitemap)) {
                            sitemapAdapter!!.remove(sitemap)
                            sitemapAdapter!!.add(sitemap)
                        }
                    }
                    sitemapAdapter!!.notifyDataSetChanged()
                }, {
                    sitemapAdapter!!.setServerState(server, SitemapAdapter.SitemapItem.STATE_ERROR)
                    logger.e(TAG, "RequestSitemap failed" ,it)
                })
    }

    companion object {

        private val TAG = "SitemapSelectorFragment"

        fun newInstance(): SitemapSelectorFragment {
            val fragment = SitemapSelectorFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
