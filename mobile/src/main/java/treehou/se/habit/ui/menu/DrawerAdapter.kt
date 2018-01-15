package treehou.se.habit.ui.menu


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap
import treehou.se.habit.R
import java.util.*

internal class DrawerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = ArrayList<DrawerItem>()
    private val sitemaps = ArrayList<OHSitemap>()
    private var itemClickListener: OnItemClickListener? = null
    private var sitemapItemClickListener: OnSitemapClickListener? = null

    internal interface OnItemClickListener {
        fun onClickItem(item: DrawerItem)
    }

    internal interface OnSitemapClickListener {
        fun onClickItem(item: OHSitemap)
    }

    internal class SitemapItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val lblName: TextView

        init {
            lblName = itemView.findViewById<View>(R.id.lbl_sitemap) as TextView
        }

        fun update(entry: OHSitemap) {
            lblName.text = entry.displayName
        }
    }

    fun setItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    fun setSitemapsClickListener(itemClickListener: OnSitemapClickListener) {
        sitemapItemClickListener = itemClickListener
    }

    /**
     * Add menu items to add.
     * @param drawerItems the items to add.
     */
    fun add(drawerItems: List<DrawerItem>) {
        items.addAll(drawerItems)
        notifyItemRangeInserted(0, drawerItems.size)
    }

    /**
     * Add sitemaps that should be shown in menu.
     *
     * @param sitemaps the sitemaps that should be shown in menu.
     */
    fun addSitemaps(sitemaps: List<OHSitemap>) {
        this.sitemaps.addAll(sitemaps)
        notifyDataSetChanged()
    }

    /**
     * Remove all sitemaps added to menu.
     */
    fun clearSitemaps() {
        sitemaps.clear()
        notifyDataSetChanged()
    }

    /**
     * Get sitemap from position.
     * @param position the menu position to get sitemap for.
     * @return sitemap at position.
     */
    private fun getSitemap(position: Int): OHSitemap {
        return sitemaps[position - (findPosition(NavigationDrawerFragment.ITEM_SITEMAPS) + 1)]
    }

    /**
     * Get sitemap from position.
     * @param position the menu position to get sitemap for.
     * @return sitemap at position.
     */
    private fun getMenuItem(position: Int): DrawerItem? {
        var mainItemPosition = 0
        for (i in 0 until itemCount) {
            val itemViewType = getItemViewType(i)

            if (VIEW_TYPE_ITEM == itemViewType) {
                val item = items[mainItemPosition]
                if (position == i) {
                    return item
                }
                mainItemPosition++
            }
        }
        return null
    }

    /**
     * Get menu item position.
     *
     * @param navigationItem the navigation item to search for.
     * @return navigation item position.
     */
    private fun findPosition(navigationItem: Int): Int {
        var mainItemPosition = 0
        for (i in 0 until itemCount) {
            val itemViewType = getItemViewType(i)

            if (VIEW_TYPE_ITEM == itemViewType) {
                val item = items[mainItemPosition]
                if (item.value == navigationItem) {
                    return i
                }
                mainItemPosition++
            }
        }
        return -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val drawerItemHolder: RecyclerView.ViewHolder
        if (VIEW_TYPE_SITEMAP == viewType) {
            val itemView = inflater.inflate(R.layout.item_sitemap_small, parent, false)
            drawerItemHolder = SitemapItemHolder(itemView)
        } else {
            val itemView = inflater.inflate(R.layout.item_drawer, parent, false)
            drawerItemHolder = DrawerItemHolder(itemView)
        }

        return drawerItemHolder
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemType = getItemViewType(position)

        if (VIEW_TYPE_SITEMAP == itemType) {
            val sitemap = getSitemap(position)
            val sitemapItemHolder = holder as SitemapItemHolder
            sitemapItemHolder.update(sitemap)
            sitemapItemHolder.itemView.setOnClickListener {
                if (sitemapItemClickListener != null) {
                    sitemapItemClickListener!!.onClickItem(sitemap)
                }
            }
        } else {
            val drawerItem = getMenuItem(position)
            val drawerItemHolder = holder as DrawerItemHolder
            drawerItemHolder.update(drawerItem!!)
            drawerItemHolder.itemView.setOnClickListener {
                if (itemClickListener != null) {
                    itemClickListener!!.onClickItem(drawerItem)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {

        var menuPosition = 0
        var i = 0
        while (i <= position) {
            val menuItem = items[menuPosition]
            if (i == position) {
                return VIEW_TYPE_ITEM
            }

            if (menuItem.value == NavigationDrawerFragment.ITEM_SITEMAPS) {
                for (sitemapPosition in sitemaps.indices) {
                    i++
                    if (i == position) {
                        return VIEW_TYPE_SITEMAP
                    }
                }
            }
            menuPosition++
            i++
        }

        return -1
    }

    override fun getItemCount(): Int {
        return items.size + sitemaps.size
    }

    companion object {

        private val VIEW_TYPE_ITEM = 1
        private val VIEW_TYPE_SITEMAP = 2
    }
}
