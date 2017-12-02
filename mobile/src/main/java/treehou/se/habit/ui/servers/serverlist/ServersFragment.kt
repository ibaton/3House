package treehou.se.habit.ui.servers.serverlist

import android.app.AlertDialog
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import javax.inject.Inject

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import io.realm.Realm
import io.realm.RealmResults
import treehou.se.habit.HabitApplication
import treehou.se.habit.R
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.module.ApplicationComponent
import treehou.se.habit.module.HasActivitySubcomponentBuilders
import treehou.se.habit.mvp.BaseDaggerFragment
import treehou.se.habit.ui.adapter.ServersAdapter
import treehou.se.habit.ui.servers.ScanServersFragment
import treehou.se.habit.ui.servers.ServerMenuFragment
import treehou.se.habit.ui.servers.SetupServerFragment
import treehou.se.habit.util.Settings

class ServersFragment : BaseDaggerFragment<ServersContract.Presenter>(), ServersContract.View {

    private var container: ViewGroup? = null
    @BindView(R.id.list) @JvmField var lstServer: RecyclerView? = null
    @BindView(R.id.empty) @JvmField var viwEmpty: View? = null
    @BindView(R.id.fab_add) @JvmField var fabAdd: FloatingActionButton? = null

    //@Inject internal var settings: Settings? = null
    //@Inject internal var presenter: ServersContract.Presenter? = null

    private var serversAdapter: ServersAdapter? = null
    private var servers: RealmResults<ServerDB>? = null
    private var unbinder: Unbinder? = null

    //private lateinit var realm: Realm

    protected val applicationComponent: ApplicationComponent
        get() = (context!!.applicationContext as HabitApplication).component()

    private val serverListener = object : ServersAdapter.ItemListener {
        override fun onItemClickListener(serverHolder: ServersAdapter.ServerHolder) {
            val server = serversAdapter!!.getItem(serverHolder.adapterPosition)
            openServerPage(server)
        }

        /**
         * Open page for editing server.
         * @param server the server to open page for.
         */
        private fun openServerPage(server: ServerDB) {
            activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.page_container, ServerMenuFragment.newInstance(server.id))
                    .addToBackStack(null)
                    .commit()
        }

        override fun onItemLongClickListener(serverHolder: ServersAdapter.ServerHolder): Boolean {

            val server = serversAdapter!!.getItem(serverHolder.adapterPosition)
            showRemoveDialog(serverHolder, server)
            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity!!.application as HabitApplication).component().inject(this)
    }

    override fun getPresenter(): ServersContract.Presenter? {
        return presenter
    }

    override fun injectMembers(hasActivitySubcomponentBuilders: HasActivitySubcomponentBuilders) {
        (hasActivitySubcomponentBuilders.getFragmentComponentBuilder(ServersFragment::class.java) as ServersComponent.Builder)
                .fragmentModule(ServersModule(this))
                .build().injectMembers(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        this.container = container
        val rootView = inflater.inflate(R.layout.fragment_servers, container, false)
        unbinder = ButterKnife.bind(this, rootView)

        val gridLayoutManager = GridLayoutManager(activity, 1)
        lstServer!!.layoutManager = gridLayoutManager
        lstServer!!.itemAnimator = DefaultItemAnimator()
        serversAdapter = ServersAdapter()
        serversAdapter!!.setItemListener(serverListener)
        lstServer!!.adapter = serversAdapter

        setupActionbar()

        return rootView
    }

    override fun onResume() {
        super.onResume()
        setupAdapter()
    }

    override fun onPause() {
        super.onPause()
        lstServer!!.adapter = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder!!.unbind()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm!!.close()
    }

    /**
     * Hookup actionbar
     */
    private fun setupActionbar() {
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setTitle(R.string.servers)
        setHasOptionsMenu(true)
    }

    /**
     * Hookup server list, listening for server updates.
     */
    private fun setupAdapter() {
        realm!!.where(ServerDB::class.java).findAllAsync().asFlowable().toObservable()
                .compose(this.bindToLifecycle<RealmResults<ServerDB>>())
                .subscribe({ servers1 ->
                    Log.d(TAG, "Loaded " + servers1.size + " servers")
                    this@ServersFragment.servers = servers1
                    updateEmptyView(servers1.size)
                    serversAdapter!!.setItems(servers1)
                })

        /*if (!settings!!.serverSetupAsked) {
            showScanServerFlow()
        }*/
    }

    /**
     * Launch flow for opening server scanning.
     */
    private fun showScanServerFlow() {
        //settings!!.serverSetupAsked = true
        AlertDialog.Builder(activity)
                .setMessage(R.string.start_scan_question)
                .setNeutralButton(R.string.new_server) { _, _ -> startNewServerFlow() }
                .setPositiveButton(R.string.scan) { _, _ -> openServerScan() }
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.servers, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_scan_for_server -> openServerScan()
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Launch flow for creating new server.
     */
    @OnClick(R.id.empty, R.id.fab_add)
    fun startNewServerFlow() {
        activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.page_container, SetupServerFragment.newInstance())
                .addToBackStack(null)
                .commit()
    }

    /**
     * Launch flow used to scan for server on network.
     */
    private fun openServerScan() {
        activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.page_container, ScanServersFragment.newInstance())
                .addToBackStack(null)
                .commit()
    }

    /**
     * Launchs flow asking user to remove or keep server.
     * @param serverHolder holder that triggered flow.
     * @param server the server to remove.
     */
    private fun showRemoveDialog(serverHolder: ServersAdapter.ServerHolder, server: ServerDB) {
        AlertDialog.Builder(activity)
                .setMessage(R.string.remove_server_question)
                .setPositiveButton(R.string.ok) { _, _ ->
                    realm!!.beginTransaction()
                    val position = serverHolder.adapterPosition
                    val i = servers!!.indexOf(server)
                    if (i >= 0) {
                        servers!!.deleteFromRealm(i)
                    }
                    realm!!.commitTransaction()
                    serversAdapter!!.notifyItemRemoved(position)
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    /**
     * Show empty view if no controllers exist
     */
    private fun updateEmptyView(itemCount: Int) {
        viwEmpty!!.visibility = if (itemCount <= 0) View.VISIBLE else View.GONE
    }

    companion object {

        private val TAG = "ServersFragment"

        fun newInstance(): ServersFragment {
            val fragment = ServersFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
