package treehou.se.habit.ui.inbox

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import java.util.ArrayList
import java.util.Collections
import java.util.Comparator

import javax.inject.Inject

import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_inbox.*
import se.treehou.ng.ohcommunicator.connector.models.OHInboxItem
import treehou.se.habit.R
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.ui.BaseFragment
import treehou.se.habit.ui.adapter.InboxAdapter
import treehou.se.habit.util.ConnectionFactory
import treehou.se.habit.util.RxUtil
import treehou.se.habit.util.Util

/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class InboxListFragment : BaseFragment() {

    @Inject lateinit var connectionFactory: ConnectionFactory

    private var relam: Realm? = null

    private var server: ServerDB? = null
    private var adapter: InboxAdapter? = null

    private val items = ArrayList<OHInboxItem>()

    private var showIgnored = false
    private var actionHide: MenuItem? = null
    private var actionShow: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Util.getApplicationComponent(this).inject(this)
        relam = Realm.getDefaultInstance()
        server = ServerDB.load(relam!!, arguments!!.getLong(ARG_SERVER))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_inbox, container, false)
        setupActionbar()
        setHasOptionsMenu(true)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hookupInboxList()
    }

    /**
     * Setup inbox list.
     */
    private fun hookupInboxList() {
        val gridLayoutManager = GridLayoutManager(activity, 1)
        listView.layoutManager = gridLayoutManager
        listView.itemAnimator = DefaultItemAnimator()

        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun getSwipeDirs(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
                return if (viewHolder is InboxAdapter.IgnoreInboxHolder) ItemTouchHelper.LEFT else super.getSwipeDirs(recyclerView, viewHolder)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                if (ItemTouchHelper.RIGHT == swipeDir) {
                    val item = adapter!!.getItem(viewHolder.adapterPosition)
                    ignoreInboxItem(item)
                }
                if (ItemTouchHelper.LEFT == swipeDir) {
                    val item = adapter!!.getItem(viewHolder.adapterPosition)
                    unignoreInboxItem(item)
                }
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(listView)

        adapter = InboxAdapter(context!!, server!!, connectionFactory)
        listView.adapter = adapter
    }

    /**
     * Set up actionbar
     */
    private fun setupActionbar() {
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setTitle(R.string.inbox)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.inbox, menu)
        actionHide = menu!!.findItem(R.id.action_hide)
        actionHide!!.setOnMenuItemClickListener {
            showIgnoredItems(false)
            true
        }

        actionShow = menu.findItem(R.id.action_show)
        actionShow!!.setOnMenuItemClickListener {
            showIgnoredItems(true)
            true
        }

        updateIgnoreButtons(showIgnored)
    }

    /**
     * Set if ignored items should be shown.
     * @param showIgnored true to show ignored items, else false.
     */
    private fun showIgnoredItems(showIgnored: Boolean) {

        this.showIgnored = showIgnored
        setItems(items, showIgnored)
        updateIgnoreButtons(showIgnored)

        val rootView = view
        if (rootView != null) {
            Snackbar.make(rootView, if (showIgnored) getString(R.string.show_ignored) else getString(R.string.hide_ignored), Snackbar.LENGTH_SHORT).show()
        }
    }

    /**
     * Update icons for showing if viewing ignored items or not.
     *
     * @param showIgnored True to show ignored ignored items else false.
     */
    private fun updateIgnoreButtons(showIgnored: Boolean) {
        actionShow!!.isVisible = !showIgnored
        actionHide!!.isVisible = showIgnored
    }

    /**
     * Set all the items that should be displayed in list.
     * Clears and updates adapter accordingly.
     *
     * @param items the items to show.
     * @param showIgnored true to filter out ignored items.
     */
    private fun setItems(items: List<OHInboxItem>, showIgnored: Boolean) {
        val inboxItems = ArrayList(items)
        Log.d(TAG, "Received items " + inboxItems)

        Collections.sort(inboxItems, InboxItemComparator())

        adapter!!.clear()
        if (!showIgnored) {
            val it = inboxItems.iterator()
            while (it.hasNext()) {
                if (it.next().isIgnored) {
                    it.remove()
                }
            }
        }
        adapter!!.addAll(inboxItems)
    }

    /**
     * Ignore inbox item.
     * Removes the inbox item from the list.
     * Sends ignore request to the server.
     *
     * @param item the item to hide.
     */
    private fun ignoreInboxItem(item: OHInboxItem) {
        item.flag = OHInboxItem.FLAG_IGNORED
        val serverHandler = connectionFactory.createServerHandler(server!!.toGeneric(), context)
        serverHandler.ignoreInboxItem(item)

        val rootView = view
        if (rootView != null) {
            Snackbar.make(rootView, R.string.hide_item, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) {
                        unignoreInboxItem(item)
                        Snackbar.make(rootView, R.string.restore_item, Snackbar.LENGTH_SHORT).show()
                    }.show()
        }
        setItems(items, showIgnored)
    }

    /**
     * Unignore inbox item.
     * Removes the inbox item from the list.
     * Sends unignore request to the server.
     *
     * @param item the item to hide.
     */
    private fun unignoreInboxItem(item: OHInboxItem) {
        item.flag = ""
        val serverHandler = connectionFactory.createServerHandler(server!!.toGeneric(), context)
        serverHandler.unignoreInboxItem(item)
        setItems(items, showIgnored)
    }

    override fun onResume() {
        super.onResume()

        showErrorView(false)
        val serverHandler = connectionFactory.createServerHandler(server!!.toGeneric(), context)
        serverHandler.requestInboxItemsRx()
                .compose(RxUtil.newToMainSchedulers())
                .compose(this.bindToLifecycle())
                .subscribe({ ohInboxItems ->
                    showErrorView(false)
                    setItems(ohInboxItems, showIgnored)
                }) {
                    logger.w(TAG, "Failed to load inbox items", it)
                    showErrorView(true)
                }
    }

    /**
     * Updates ui to show error.
     * @param showError the error to show.
     */
    private fun showErrorView(showError: Boolean) {
        if (showError) {
            empty.visibility = View.VISIBLE
            listView.visibility = View.GONE
        } else {
            empty.visibility = View.GONE
            listView.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        relam!!.close()
    }

    /**
     * Comparator for sorting inbox item in list.
     */
    private class InboxItemComparator : Comparator<OHInboxItem> {
        override fun compare(lhs: OHInboxItem, rhs: OHInboxItem): Int {

            val inboxCompare = lhs.label.compareTo(rhs.label)
            return if (inboxCompare == 0) {
                lhs.thingUID.compareTo(rhs.thingUID)
            } else inboxCompare

        }
    }

    companion object {

        private val TAG = "InboxListFragment"

        private val ARG_SERVER = "argServer"

        /**
         * Creates a new instance of inbox list fragment.
         *
         * @param serverId the server to connect to.
         * @return new fragment instance.
         */
        fun newInstance(serverId: Long): InboxListFragment {
            val fragment = InboxListFragment()
            val args = Bundle()
            args.putLong(ARG_SERVER, serverId)
            fragment.arguments = args
            return fragment
        }
    }
}
