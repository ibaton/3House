package treehou.se.habit.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap
import treehou.se.habit.R
import java.util.*

class SitemapListAdapter : RecyclerView.Adapter<SitemapListAdapter.SitemapBaseHolder>() {

    private val items = HashMap<OHServer, SitemapItem>()
    private var sitemapSelectedListener: SitemapSelectedListener = DummySitemapSelectListener()

    enum class ServerState {
        STATE_SUCCESS, STATE_LOADING, STATE_ERROR, STATE_CERTIFICATE_ERROR
    }

    class SitemapItem(var server: OHServer) {
        var state: ServerState = ServerState.STATE_LOADING
        var sitemaps: MutableList<OHSitemap> = ArrayList()

        fun addItem(sitemap: OHSitemap) {
            sitemaps.add(sitemap)
            state = ServerState.STATE_SUCCESS
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

    interface SitemapSelectedListener {
        fun onSelected(server: OHServer, sitemap: OHSitemap?)
        fun onErrorSelected(server: OHServer)
        fun onCertificateErrorSelected(server: OHServer)
    }

    internal inner class DummySitemapSelectListener : SitemapSelectedListener {
        override fun onSelected(server: OHServer, sitemap: OHSitemap?) {}
        override fun onErrorSelected(server: OHServer) {}
        override fun onCertificateErrorSelected(server: OHServer) {

        }
    }

    inner class SitemapErrorHolder(view: View) : SitemapBaseHolder(view)

    inner class SitemapCertificateErrorHolder(view: View) : SitemapBaseHolder(view)

    inner class SitemapLoadHolder(view: View) : SitemapBaseHolder(view)

    inner class GetResult(var item: SitemapItem, var sitemap: OHSitemap?)

    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int): SitemapBaseHolder {
        val type = ServerState.values()[itemType]
        val inflater = LayoutInflater.from(parent.context)
        if (ServerState.STATE_SUCCESS == type) {
            val itemView = inflater.inflate(R.layout.item_sitemap, parent, false)
            return SitemapHolder(itemView)
        } else if (ServerState.STATE_LOADING == type) {
            val itemView = inflater.inflate(R.layout.item_sitemap_load, parent, false)
            return SitemapLoadHolder(itemView)
        } else if (ServerState.STATE_CERTIFICATE_ERROR == type) {
            val itemView = inflater.inflate(R.layout.item_sitemap_certificate_failed, parent, false)
            return SitemapCertificateErrorHolder(itemView)
        } else {
            val serverLoadFail = inflater.inflate(R.layout.item_sitemap_failed, parent, false)
            return SitemapErrorHolder(serverLoadFail)
        }
    }

    override fun onBindViewHolder(sitemapHolder: SitemapBaseHolder, position: Int) {

        val type = ServerState.values()[getItemViewType(position)]
        val item = getItem(position)

        val sitemap = item!!.sitemap
        val server = item.item.server
        if (ServerState.STATE_SUCCESS == type) {
            val holder = sitemapHolder as SitemapHolder
            holder.lblName.text = sitemap?.displayName
            holder.lblServer.text = server.displayName
            sitemapHolder.itemView.setOnClickListener { v -> sitemapSelectedListener.onSelected(server, sitemap) }
        } else if (ServerState.STATE_LOADING == type) {
            val holder = sitemapHolder as SitemapLoadHolder
            holder.lblServer.text = server.displayName
        } else if (ServerState.STATE_ERROR == type) {
            val holder = sitemapHolder as SitemapErrorHolder
            holder.lblServer.text = server.displayName
            holder.itemView.setOnClickListener { v -> sitemapSelectedListener.onErrorSelected(server) }
        } else if (ServerState.STATE_CERTIFICATE_ERROR == type) {
            val holder = sitemapHolder as SitemapCertificateErrorHolder
            holder.lblServer.text = server.displayName
            holder.itemView.setOnClickListener { v -> sitemapSelectedListener.onCertificateErrorSelected(server) }
        }
    }

    override fun getItemViewType(position: Int): Int {
        var count = 0
        for (item in items.values) {
            if (ServerState.STATE_SUCCESS == item.state) {
                if (position >= count && position < count + item.sitemaps.size) {
                    return ServerState.STATE_SUCCESS.ordinal
                }
                count += item.sitemaps.size
            } else if (ServerState.STATE_ERROR == item.state) {
                if (count == position) {
                    return ServerState.STATE_ERROR.ordinal
                }
                count++
            } else if (ServerState.STATE_CERTIFICATE_ERROR == item.state) {
                if (count == position) {
                    return ServerState.STATE_CERTIFICATE_ERROR.ordinal
                }
                count++
            } else if (ServerState.STATE_LOADING == item.state) {
                if (count == position) {
                    return ServerState.STATE_LOADING.ordinal
                }
                count++
            }
        }

        return ServerState.STATE_LOADING.ordinal
    }

    override fun getItemCount(): Int {

        var count = 0
        for (item in items.values) {
            if (item.state == ServerState.STATE_SUCCESS) {
                count += item.sitemaps.size
            } else {
                count++
            }
        }

        return count
    }

    /**
     * Returns item at a certain position
     *
     * @param position item to grab item for
     * @return
     */
    fun getItem(position: Int): GetResult? {
        var result: GetResult? = null
        var count = 0
        for (item in items.values) {
            if (ServerState.STATE_SUCCESS == item.state) {
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

    fun addAll(server: OHServer, sitemapIds: List<OHSitemap>) {
        for (sitemap in sitemapIds) {
            add(server, sitemap)
        }
    }

    fun add(server: OHServer, sitemap: OHSitemap) {
        var item: SitemapItem? = items[server]
        if (item == null) {
            item = SitemapItem(server)
            items.put(item.server, item)
        }
        item.addItem(sitemap)

        notifyDataSetChanged()
    }

    /**
     * Remove all sitemap entries from adapter.
     */
    fun clear() {
        items.clear()
        notifyItemRangeRemoved(0, items.size - 1)
    }

    fun remove(sitemap: OHSitemap) {
        val pos = findPosition(sitemap)
        remove(sitemap, pos)
    }

    fun remove(sitemap: OHSitemap?, position: Int) {
        val item: SitemapItem? = null // TODO items.get(serverDB.getId());
        if (sitemap == null) {
            return
        }

        item!!.sitemaps.remove(sitemap)
        notifyItemRemoved(position)
    }

    /**
     * Add listener for when sitemap item is clicked
     *
     * @param sitemapSelectedListener listens for click on sitemap.
     */
    fun setSitemapSelectedListener(sitemapSelectedListener: SitemapSelectedListener?) {
        if (sitemapSelectedListener == null) {
            this.sitemapSelectedListener = DummySitemapSelectListener()
            return
        }
        this.sitemapSelectedListener = sitemapSelectedListener

    }

    private fun findPosition(sitemap: OHSitemap): Int {
        var count = 0
        for (item in items.values) {
            if (ServerState.STATE_SUCCESS == item.state) {
                for (sitemapIter in item.sitemaps) {
                    if (sitemap === sitemapIter) {
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

    /**
     * Get a sitemap item. Creates a new server item if no item exists.
     *
     * @param server server to get sitemap item for.
     * @return
     */
    private fun getItem(server: OHServer): SitemapItem {
        var item: SitemapItem? = items[server]
        if (item == null) {
            item = SitemapItem(server)
            items.put(server, item)
        }
        return item
    }

    fun setServerState(server: OHServer, state: ServerState) {
        val item = getItem(server)
        item.state = state

        notifyDataSetChanged()
    }

    operator fun contains(sitemap: OHSitemap): Boolean {
        return items.containsKey(sitemap.server) && items[sitemap.server]?.sitemaps!!.contains(sitemap)
    }
}
