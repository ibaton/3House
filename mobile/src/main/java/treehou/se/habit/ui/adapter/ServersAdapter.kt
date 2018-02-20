package treehou.se.habit.ui.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.realm.OrderedRealmCollectionSnapshot
import io.realm.RealmResults
import treehou.se.habit.R
import treehou.se.habit.core.db.model.ServerDB

class ServersAdapter : RecyclerView.Adapter<ServersAdapter.ServerHolder>() {

    private var realmResults: OrderedRealmCollectionSnapshot<ServerDB>? = null
    private var itemListener: ItemListener = DummyItemListener()

    private val ITEM_TYPE_SERVER = 1
    private val ITEM_TYPE_MY_OPENHAB_SERVER = 2

    inner class ServerHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lblName: TextView

        init {
            lblName = view.findViewById<View>(R.id.lbl_server) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerHolder {
        val inflater = LayoutInflater.from(parent.context)

        if (viewType == ITEM_TYPE_MY_OPENHAB_SERVER) {
            val itemView = inflater.inflate(R.layout.item_my_openhab_server, parent, false)
            return ServerHolder(itemView)
        }

        val itemView = inflater.inflate(R.layout.item_server, parent, false)
        return ServerHolder(itemView)
    }

    override fun onBindViewHolder(serverHolder: ServerHolder, position: Int) {
        val server = realmResults!![position]
        if(server?.isValid == false) return

        serverHolder.lblName.text = server!!.displayName
        serverHolder.itemView.setOnClickListener { _ -> itemListener.onItemClickListener(serverHolder) }
        serverHolder.itemView.setOnLongClickListener { _ -> itemListener.onItemLongClickListener(serverHolder) }
    }

    fun setItems(realmResults: OrderedRealmCollectionSnapshot<ServerDB>) {
        this.realmResults = realmResults
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (realmResults == null) 0 else realmResults!!.size
    }

    fun getItem(position: Int): ServerDB? {
        return realmResults!![position]
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        if (item != null && item.isValid && item.isMyOpenhabServer) {
            return ITEM_TYPE_MY_OPENHAB_SERVER
        }
        return ITEM_TYPE_SERVER
    }

    interface ItemListener {

        fun onItemClickListener(serverHolder: ServerHolder)

        fun onItemLongClickListener(serverHolder: ServerHolder): Boolean
    }

    inner class DummyItemListener : ItemListener {

        override fun onItemClickListener(serverHolder: ServerHolder) {}

        override fun onItemLongClickListener(serverHolder: ServerHolder): Boolean {
            return false
        }
    }

    /**
     * Add adapter change listener
     * @param itemListener
     */
    fun setItemListener(itemListener: ItemListener?) {
        if (itemListener == null) {
            this.itemListener = DummyItemListener()
            return
        }
        this.itemListener = itemListener
    }
}
