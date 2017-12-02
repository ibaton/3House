package treehou.se.habit.ui.servers

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.services.Scanner
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback
import se.treehou.ng.ohcommunicator.services.callbacks.OHResponse
import treehou.se.habit.R
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.ui.BaseFragment

class ScanServersFragment : BaseFragment() {

    @BindView(R.id.empty) @JvmField var viwEmpty: View? = null
    @BindView(R.id.list) @JvmField var lstServer: RecyclerView? = null

    private lateinit var serversAdapter: ServersAdapter
    private var discoveryListener: OHCallback<List<OHServer>>? = null
    private var unbinder: Unbinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context = context
        serversAdapter = ServersAdapter(context!!)

        // TODO serversAdapter.addAll(OHServer.loadAll());
        serversAdapter.setItemListener(object : ItemListener {
            override fun onItemClickListener(serverHolder: ServersAdapter.ServerHolder) {
                val server = serversAdapter.getItem(serverHolder.adapterPosition)
                saveServer(server)
                fragmentManager!!.popBackStack()
            }

            override fun itemCountUpdated(itemCount: Int) {
                updateEmptyView(itemCount)
            }
        })
    }

    private fun saveServer(server: OHServer) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val serverDB = ServerDB.fromGeneric(server)
        realm.copyToRealmOrUpdate(serverDB)
        realm.commitTransaction()
        realm.close()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val rootView = inflater.inflate(R.layout.fragment_scan_servers, container, false)
        unbinder = ButterKnife.bind(this, rootView)

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setTitle(R.string.scan_for_server)

        lstServer!!.adapter = serversAdapter
        val gridLayoutManager = GridLayoutManager(activity, 1)
        lstServer!!.layoutManager = gridLayoutManager
        lstServer!!.itemAnimator = DefaultItemAnimator()

        return rootView
    }

    override fun onResume() {
        super.onResume()

        serversAdapter.clear()
        discoveryListener = object : OHCallback<List<OHServer>> {
            override fun onUpdate(response: OHResponse<List<OHServer>>) {
                if (isAdded) {
                    activity!!.runOnUiThread {
                        for (server in response.body()) {
                            serversAdapter.addItem(server)
                        }
                    }
                }
            }

            override fun onError() {
                logger.e(TAG, "Server discovery failed")
            }
        }

        val scanner = Scanner(context)
        scanner.registerRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { server -> serversAdapter.addItem(server) }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        unbinder!!.unbind()
    }

    /**
     * Show empty view if no controllers exist
     */
    private fun updateEmptyView(itemCount: Int) {
        viwEmpty!!.visibility = if (itemCount <= 0) View.VISIBLE else View.GONE
    }

    class ServersAdapter(private val context: Context) : RecyclerView.Adapter<ServersAdapter.ServerHolder>() {

        private val items = ArrayList<OHServer>()

        private var itemListener: ItemListener = DummyItemListener()

        inner class ServerHolder(view: View) : RecyclerView.ViewHolder(view) {
            val lblName: TextView
            val lblHost: TextView

            init {
                lblName = view.findViewById<View>(R.id.lbl_server) as TextView
                lblHost = view.findViewById<View>(R.id.lbl_host) as TextView
            }
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ServerHolder {

            val inflater = LayoutInflater.from(context)
            val itemView = inflater.inflate(R.layout.item_scan_server, null)

            return ServerHolder(itemView)
        }

        override fun onBindViewHolder(serverHolder: ServerHolder, position: Int) {
            val server = items[position]

            serverHolder.lblName.text = server.displayName
            serverHolder.lblHost.text = server.localUrl
            serverHolder.itemView.setOnClickListener { _ -> itemListener.onItemClickListener(serverHolder) }
        }

        override fun getItemCount(): Int {
            return items.size
        }

        fun getItem(position: Int): OHServer {
            return items[position]
        }

        inner class DummyItemListener : ItemListener {

            override fun onItemClickListener(serverHolder: ServerHolder) {}

            override fun itemCountUpdated(itemCount: Int) {}
        }

        fun setItemListener(itemListener: ItemListener?) {
            if (itemListener == null) {
                this.itemListener = DummyItemListener()
                return
            }
            this.itemListener = itemListener
        }

        fun addItem(item: OHServer) {
            if (items.contains(item)) {
                return
            }
            items.add(0, item)
            notifyItemInserted(0)
            itemListener.itemCountUpdated(items.size)
        }

        fun addAll(items: MutableList<OHServer>) {
            val serverIterator = items.iterator()
            while (serverIterator.hasNext()) {
                val serverDB = serverIterator.next()
                if (this.items.contains(serverDB)) {
                    serverIterator.remove()
                }
            }

            for (item in items) {
                this.items.add(0, item)
            }
            notifyItemRangeInserted(0, items.size)
            itemListener.itemCountUpdated(items.size)
        }

        fun removeItem(position: Int) {
            Log.d(TAG, "removeItem: " + position)
            items.removeAt(position)
            notifyItemRemoved(position)
            itemListener.itemCountUpdated(items.size)
        }

        fun removeItem(item: OHServer) {
            val position = items.indexOf(item)
            items.removeAt(position)
            itemListener.itemCountUpdated(items.size)
        }

        fun clear() {
            this.items.clear()
            notifyDataSetChanged()
            itemListener.itemCountUpdated(items.size)
        }
    }

    interface ItemListener {

        fun onItemClickListener(serverHolder: ScanServersFragment.ServersAdapter.ServerHolder)

        fun itemCountUpdated(itemCount: Int)
    }

    companion object {

        private val TAG = "ScanServersFragment"

        fun newInstance(): ScanServersFragment {
            val fragment = ScanServersFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
