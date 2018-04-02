package treehou.se.habit.ui.adapter

import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import java.util.ArrayList

import se.treehou.ng.ohcommunicator.connector.models.OHInboxItem
import treehou.se.habit.R
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.util.ConnectionFactory

class InboxAdapter(private val context: Context, private val server: ServerDB, private val connectionFactory: ConnectionFactory) : RecyclerView.Adapter<InboxAdapter.InboxHolder>() {

    private val items = ArrayList<OHInboxItem>()
    private var itemListener: ItemListener = DummyItemListener()

    enum class ItemType {
        ITEM, ITEM_IGNORED
    }

    open inner class InboxHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lblName: TextView
        var louProperties: LinearLayout

        init {
            lblName = view.findViewById<View>(R.id.lbl_server) as TextView
            louProperties = itemView.findViewById<View>(R.id.lou_properties) as LinearLayout
        }
    }

    inner class IgnoreInboxHolder(view: View) : InboxHolder(view)

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item.isIgnored) {
            ItemType.ITEM_IGNORED.ordinal
        } else {
            ItemType.ITEM.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InboxHolder {
        val type = ItemType.values()[viewType]
        val inflater = LayoutInflater.from(parent.context)
        var inboxHolder: InboxHolder? = null
        when (type) {
            ItemType.ITEM -> {
                val itemView = inflater.inflate(R.layout.item_inbox, parent, false)
                inboxHolder = InboxHolder(itemView)
            }
            ItemType.ITEM_IGNORED -> {
                val itemView = inflater.inflate(R.layout.item_inbox_ignored, parent, false)
                inboxHolder = IgnoreInboxHolder(itemView)
            }
        }
        return inboxHolder
    }

    override fun onBindViewHolder(serverHolder: InboxHolder, position: Int) {
        val inboxItem = items[position]

        serverHolder.lblName.text = inboxItem.label
        serverHolder.itemView.setOnClickListener { v -> itemListener.onItemClickListener(serverHolder) }
        serverHolder.itemView.setOnLongClickListener { v -> itemListener.onItemLongClickListener(serverHolder) }

        val louProperties = serverHolder.louProperties
        louProperties.removeAllViews()

        val inflater = LayoutInflater.from(context)
        for ((key, value) in inboxItem.properties) {
            val louProperty = inflater.inflate(R.layout.item_property, louProperties, false)
            val lblProperty = louProperty.findViewById<View>(R.id.lbl_property) as TextView
            lblProperty.text = context.getString(R.string.inbox_property, key, value)

            louProperties.addView(louProperty)
        }

        val serverHandler = connectionFactory.createServerHandler(server.toGeneric(), context)
        serverHolder.itemView.setOnClickListener { v ->
            AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.approve_item))
                    .setMessage(context.getString(R.string.approve_this_item))
                    .setPositiveButton(R.string.ok) { dialog, which -> serverHandler.approveInboxItem(inboxItem) }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getItem(position: Int): OHInboxItem {
        return items[position]
    }

    interface ItemListener {

        fun onItemClickListener(serverHolder: InboxHolder)

        fun onItemLongClickListener(serverHolder: InboxHolder): Boolean

        fun itemCountUpdated(itemCount: Int)
    }

    inner class DummyItemListener : ItemListener {

        override fun onItemClickListener(serverHolder: InboxHolder) {}

        override fun onItemLongClickListener(serverHolder: InboxHolder): Boolean {
            return false
        }

        override fun itemCountUpdated(itemCount: Int) {}
    }

    fun setItemListener(itemListener: ItemListener?) {
        if (itemListener == null) {
            this.itemListener = DummyItemListener()
            return
        }
        this.itemListener = itemListener
    }

    fun addItem(item: OHInboxItem) {
        items.add(0, item)
        notifyItemInserted(0)
        itemListener.itemCountUpdated(items.size)
    }

    fun addAll(items: List<OHInboxItem>) {
        for (item in items) {
            this.items.add(0, item)
        }
        notifyDataSetChanged()
        itemListener.itemCountUpdated(items.size)
    }

    fun removeItem(position: Int) {
        Log.d(TAG, "removeItem: " + position)
        items.removeAt(position)
        notifyItemRemoved(position)
        itemListener.itemCountUpdated(items.size)
    }

    fun removeItem(item: OHInboxItem) {
        val position = items.indexOf(item)
        items.removeAt(position)
        notifyItemRemoved(position)
        itemListener.itemCountUpdated(items.size)
    }

    /**
     * Remove all items from adapter
     */
    fun clear() {
        this.items.clear()
        notifyDataSetChanged()
        itemListener.itemCountUpdated(items.size)
    }

    companion object {

        private val TAG = InboxAdapter::class.java.simpleName
    }
}
