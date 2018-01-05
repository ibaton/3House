package treehou.se.habit.ui.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import java.util.ArrayList

import se.treehou.ng.ohcommunicator.connector.models.OHLink
import treehou.se.habit.R

class LinkAdapter : RecyclerView.Adapter<LinkAdapter.LinkHolder>() {

    private val items = ArrayList<OHLink>()

    private var itemListener: ItemListener = DummyItemListener()

    inner class LinkHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val lblItem: TextView
        private val lblChannel: TextView

        init {
            lblItem = view.findViewById<View>(R.id.lbl_item) as TextView
            lblChannel = itemView.findViewById<View>(R.id.lbl_channel) as TextView
        }

        fun update(link: OHLink) {
            lblChannel.text = link.channelUID
            lblItem.text = link.itemName
            itemView.setOnClickListener { view -> itemListener.onItemClickListener(link) }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): LinkHolder {
        return LinkHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_link, viewGroup, false))
    }

    override fun onBindViewHolder(holder: LinkHolder, position: Int) {
        val item = items[position]
        holder.update(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItemListener(itemListener: ItemListener?) {
        if (itemListener == null) {
            this.itemListener = DummyItemListener()
            return
        }
        this.itemListener = itemListener
    }

    fun addItem(item: OHLink) {
        items.add(0, item)
        notifyItemInserted(0)
    }

    fun addAll(items: List<OHLink>) {
        for (item in items) {
            this.items.add(0, item)
        }
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        Log.d(TAG, "removeItem: " + position)
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeItem(item: OHLink) {
        val position = items.indexOf(item)
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * Remove all items from adapter
     */
    fun clear() {
        this.items.clear()
        notifyDataSetChanged()
    }

    interface ItemListener {

        fun onItemClickListener(item: OHLink)

        fun onItemLongClickListener(item: OHLink): Boolean
    }

    private inner class DummyItemListener : ItemListener {

        override fun onItemClickListener(item: OHLink) {}

        override fun onItemLongClickListener(item: OHLink): Boolean {
            return false
        }
    }

    companion object {

        private val TAG = LinkAdapter::class.java.simpleName
    }
}
