package treehou.se.habit.ui.links

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_links_list.*
import okhttp3.ResponseBody
import retrofit2.Response
import se.treehou.ng.ohcommunicator.connector.models.OHLink
import treehou.se.habit.HabitApplication
import treehou.se.habit.R
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.ui.BaseFragment
import treehou.se.habit.ui.adapter.LinkAdapter
import treehou.se.habit.util.ConnectionFactory
import javax.inject.Inject

/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class LinksListFragment : BaseFragment() {

    @Inject lateinit var connectionFactory: ConnectionFactory

    private var adapter: LinkAdapter = LinkAdapter()
    private var server: ServerDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity!!.application as HabitApplication).component().inject(this)

        realm = Realm.getDefaultInstance()
        val serverId = arguments!!.getLong(ARG_SERVER)
        server = ServerDB.load(realm, serverId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_links_list, container, false)
        setupActionBar()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.clear()
        listView.layoutManager = LinearLayoutManager(activity)

        adapter.setItemListener(object : LinkAdapter.ItemListener {
            override fun onItemClickListener(item: OHLink) {
                openRemoveLinkDialog(item)
            }

            override fun onItemLongClickListener(item: OHLink): Boolean {
                return false
            }
        })
        listView.adapter = adapter
    }

    /**
     * Ask user if link should be removed.
     * @param link the link to remove.
     */
    private fun openRemoveLinkDialog(link: OHLink) {
        AlertDialog.Builder(context!!)
                .setMessage(R.string.remove_link)
                .setPositiveButton(android.R.string.ok) { dialogInterface, _ -> removeLink(link) }
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show()
    }

    /**
     * Remove link.
     * @param link the link to remove
     */
    private fun removeLink(link: OHLink) {
        adapter.removeItem(link)

        connectionFactory.createServerHandler(server!!.toGeneric(), context)
                .deleteLinkRx(link)
                .compose<Response<ResponseBody>>(bindToLifecycle<Response<ResponseBody>>())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    adapter.addItem(link)
                    Toast.makeText(context, R.string.failed_delete_link, Toast.LENGTH_SHORT).show()
                }) { logger.e(TAG, "removeLink Failed", it) }
    }

    /**
     * Setup actionbar.
     */
    private fun setupActionBar() {
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setTitle(R.string.links)
    }

    /**
     * Clears list of sitemaps.
     */
    private fun clearList() {
        emptyView.visibility = View.VISIBLE
        adapter.clear()
    }

    override fun onResume() {
        super.onResume()

        loadLinks()
    }

    /**
     * Load servers from database and request their sitemaps.
     */
    private fun loadLinks() {
        connectionFactory.createServerHandler(server!!.toGeneric(), context)
                .requestLinksRx()
                .filter { ohLinks -> ohLinks.size > 0 }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe({ ohLinks ->
                    clearList()
                    emptyView.visibility = View.GONE
                    adapter.addAll(ohLinks)
                }) { logger.w(TAG, "Failed to load link items", it) }
    }

    companion object {

        private val TAG = LinksListFragment::class.java.simpleName

        private val ARG_SERVER = "ARG_SERVER"

        /**
         * Create fragment where user can select sitemap.
         *
         * @return Fragment
         */
        fun newInstance(serverId: Long): LinksListFragment {
            val fragment = LinksListFragment()
            val args = Bundle()
            args.putLong(ARG_SERVER, serverId)
            fragment.arguments = args
            return fragment
        }
    }
}
