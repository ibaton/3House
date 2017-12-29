package treehou.se.habit.ui.sitemaps

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import javax.inject.Inject

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap
import se.treehou.ng.ohcommunicator.services.Connector
import se.treehou.ng.ohcommunicator.services.IServerHandler
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback
import se.treehou.ng.ohcommunicator.services.callbacks.OHResponse
import treehou.se.habit.R
import treehou.se.habit.ui.BaseFragment
import treehou.se.habit.ui.adapter.SitemapAdapter
import treehou.se.habit.util.ConnectionFactory
import treehou.se.habit.util.Util

class SitemapSelectorFragment : BaseFragment() {

    @BindView(R.id.list) lateinit var mListView: RecyclerView

    @Inject lateinit var connectionFactory: ConnectionFactory

    private var mSitemapAdapter: SitemapAdapter? = null
    private var unbinder: Unbinder? = null

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

        mSitemapAdapter = SitemapAdapter()
        mSitemapAdapter!!.setSelectorListener(sitemapSelectListener)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_sitemap_selector, container, false)
        unbinder = ButterKnife.bind(this, rootView)

        val gridLayoutManager = GridLayoutManager(activity, 1)

        mListView.layoutManager = gridLayoutManager
        mListView.itemAnimator = DefaultItemAnimator()
        mListView.adapter = mSitemapAdapter

        return rootView
    }

    override fun onResume() {
        super.onResume()

        mSitemapAdapter!!.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder!!.unbind()
    }

    /**
     * Request sitemaps for server.
     * @param server the server to request sitemap for.
     */
    private fun requestSitemap(server: OHServer) {
        mSitemapAdapter!!.setServerState(server, SitemapAdapter.SitemapItem.STATE_LOADING)
        val serverHandler = connectionFactory.createServerHandler(server, context)

        serverHandler.requestSitemapRx()
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ sitemaps ->
                    for (sitemap in sitemaps) {
                        sitemap.server = server
                        if (!mSitemapAdapter!!.contains(sitemap)) {
                            mSitemapAdapter!!.add(sitemap)
                        } else if (OHSitemap.isLocal(sitemap)) {
                            mSitemapAdapter!!.remove(sitemap)
                            mSitemapAdapter!!.add(sitemap)
                        }
                    }
                    mSitemapAdapter!!.notifyDataSetChanged()
                }) { mSitemapAdapter!!.setServerState(server, SitemapAdapter.SitemapItem.STATE_ERROR) }
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
