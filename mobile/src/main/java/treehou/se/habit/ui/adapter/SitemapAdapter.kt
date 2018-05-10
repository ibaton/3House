package treehou.se.habit.ui.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import java.util.ArrayList
import java.util.HashMap

import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap
import treehou.se.habit.R

class SitemapAdapter : RecyclerView.Adapter<SitemapAdapter.SitemapBaseHolder>() {
    private val items = HashMap<OHServer, SitemapItem>()
    private var selectorListener: OnSitemapSelectListener? = null

    class SitemapItem(var server: OHServer) {
        var state = STATE_LOADING
        var sitemaps: MutableList<OHSitemap> = ArrayList()

        fun addItem(sitemap: OHSitemap) {
            sitemaps.add(sitemap)
            state = STATE_SUCCESS
        }

        companion object {

            val STATE_SUCCESS = 0
            val STATE_LOADING = 1
            val STATE_ERROR = 2
        }
    }

    open inner class SitemapBaseHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var lblServer: TextView

        init {
            lblServer = itemView.findViewById<View>(R.id.lbl_server) as TextView
        }
    }

    inner class SitemapHolder(view: View) : SitemapBaseHolder(view) {
        var lblName: TextView

        init {

            lblName = itemView.findViewById<View>(R.id.lbl_sitemap) as TextView
        }
    }

    inner class SitemapErrorHolder(view: View) : SitemapBaseHolder(view)

    inner class SitemapLoadHolder(view: View) : SitemapBaseHolder(view)

    inner class GetResult(var item: SitemapItem, var sitemap: OHSitemap?)

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): SitemapBaseHolder {

        val inflater = LayoutInflater.from(parent.context)
        if (SitemapItem.STATE_SUCCESS == type) {
            val itemView = inflater.inflate(R.layout.item_sitemap, parent, false)
            return SitemapHolder(itemView)
        } else if (SitemapItem.STATE_LOADING == type) {
            val itemView = inflater.inflate(R.layout.item_sitemap_load, parent, false)
            return SitemapLoadHolder(itemView)
        } else {
            val serverLoadFail = inflater.inflate(R.layout.item_sitemap_failed, parent, false)
            return SitemapErrorHolder(serverLoadFail)
        }
    }

    override fun onBindViewHolder(sitemapHolder: SitemapBaseHolder, position: Int) {

        val type = getItemViewType(position)
        val item = getItem(position)

        if (SitemapItem.STATE_SUCCESS == type) {
            val holder = sitemapHolder as SitemapHolder
            val sitemap = item!!.sitemap

            holder.lblName.text = item.sitemap?.label
            holder.lblServer.text = item.sitemap?.server?.name

            sitemapHolder.itemView.setOnClickListener { selectorListener!!.onSitemapSelect(sitemap!!) }
        } else if (SitemapItem.STATE_LOADING == type) {
            val holder = sitemapHolder as SitemapLoadHolder
            holder.lblServer.text = item!!.item.server.name
        } else if (SitemapItem.STATE_ERROR == type) {
            val holder = sitemapHolder as SitemapErrorHolder
            holder.lblServer.text = item!!.item.server.name
            holder.itemView.setOnClickListener { selectorListener!!.onErrorClicked(item.item.server) }
        }
    }

    interface OnSitemapSelectListener {
        fun onSitemapSelect(sitemap: OHSitemap)
        fun onErrorClicked(server: OHServer)
    }

    private inner class DummySelectListener : OnSitemapSelectListener {
        override fun onSitemapSelect(sitemap: OHSitemap) {}

        override fun onErrorClicked(server: OHServer) {}
    }

    override fun getItemViewType(position: Int): Int {
        var count = 0
        for (item in items.values) {
            if (SitemapItem.STATE_SUCCESS == item.state) {
                if (position >= count && position < count + item.sitemaps.size) {
                    return SitemapItem.STATE_SUCCESS
                }
                count += item.sitemaps.size
            } else if (SitemapItem.STATE_ERROR == item.state) {
                if (count == position) {
                    return SitemapItem.STATE_ERROR
                }
                count++
            } else if (SitemapItem.STATE_LOADING == item.state) {
                if (count == position) {
                    return SitemapItem.STATE_LOADING
                }
                count++
            }
        }

        return SitemapItem.STATE_LOADING
    }

    override fun getItemCount(): Int {

        var count = 0
        for (item in items.values) {
            if (item.state == SitemapItem.STATE_SUCCESS) {
                count += item.sitemaps.size
            } else {
                count++
            }
        }

        return count
    }

    fun getItem(position: Int): GetResult? {
        var result: GetResult? = null
        var count = 0
        for (item in items.values) {
            if (SitemapItem.STATE_SUCCESS == item.state) {
                for (sitemap in item.sitemaps) {
                    if (count == position) {
                        result = GetResult(item, sitemap)
                        return result
                    }
                    count++
                }
            } else {
                if (count == position) {
                    result = GetResult(item, null)
                    break
                }
                count++
            }
        }

        return result
    }

    fun addAll(sitemaps: List<OHSitemap>) {
        for (sitemap in sitemaps) {
            add(sitemap)
        }
    }

    fun add(sitemap: OHSitemap) {
        var item: SitemapItem? = items[sitemap.server]
        if (item == null) {
            item = SitemapItem(sitemap.server)
            items.put(item.server, item)
        }

        val count = itemCount
        item.addItem(sitemap)
        Log.d(TAG, "Added sitemap " + sitemap.server.name + " " + sitemap.name + " precount: " + count + " postcount: " + itemCount + " items: " + items.size)

        notifyDataSetChanged()
    }

    fun remove(sitemap: OHSitemap) {
        val pos = findPosition(sitemap)
        remove(sitemap, pos)
    }

    fun remove(sitemap: OHSitemap, position: Int) {
        val item = items[sitemap.server] ?: return

        item.sitemaps.remove(sitemap)
        notifyItemRemoved(position)
    }

    private fun findPosition(pSitemap: OHSitemap): Int {
        var count = 0
        for (item in items.values) {
            if (SitemapItem.STATE_SUCCESS == item.state) {
                for (sitemap in item.sitemaps) {
                    if (sitemap === pSitemap) {
                        return count
                    }
                    count++
                }
            } else {
                count++
            }
        }
        return -1
    }

    fun setSelectorListener(selectorListener: OnSitemapSelectListener?) {
        var selectorListener = selectorListener
        if (selectorListener == null) selectorListener = DummySelectListener()
        this.selectorListener = selectorListener
    }

    fun setServerState(server: OHServer, state: Int) {
        var item: SitemapItem? = items[server]
        if (item == null) {
            item = SitemapItem(server)
            items.put(server, item)
        }
        item.state = state

        notifyDataSetChanged()
    }

    operator fun contains(sitemap: OHSitemap): Boolean {
        return items.containsKey(sitemap.server) && items[sitemap.server]?.sitemaps!!.contains(sitemap)
    }

    fun clear() {
        val last = items.size - 1
        items.clear()
        notifyItemRangeRemoved(0, last)
    }

    companion object {

        private val TAG = SitemapAdapter::class.java.simpleName
    }
}
