package treehou.se.habit.ui.servers

import android.os.Bundle
import android.support.annotation.IntDef
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import treehou.se.habit.HabitApplication
import treehou.se.habit.R
import treehou.se.habit.module.ApplicationComponent
import treehou.se.habit.ui.adapter.ImageItem
import treehou.se.habit.ui.adapter.ImageItemAdapter
import treehou.se.habit.ui.bindings.BindingsFragment
import treehou.se.habit.ui.inbox.InboxListFragment
import treehou.se.habit.ui.links.LinksListFragment
import treehou.se.habit.ui.servers.create.custom.SetupServerFragment
import treehou.se.habit.ui.servers.sitemaps.list.SitemapSelectFragment
import java.util.*

/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class ServerMenuFragment : Fragment() {

    private var unbinder: Unbinder? = null
    private var serverId: Long = 0

    private var container: ViewGroup? = null

    /**
     * The fragment's ListView/GridView.
     */
    @BindView(R.id.list)
    lateinit var listView: RecyclerView

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private lateinit var adapter: ImageItemAdapter

    protected val applicationComponent: ApplicationComponent
        get() = (context!!.applicationContext as HabitApplication).component()

    private val optionsSelectListener = { id: Int ->

        var fragment: Fragment? = null
        when (id) {
            ITEM_EDIT -> fragment = SetupServerFragment.newInstance(serverId)
            ITEM_INBOX -> openInboxPage(serverId)
            ITEM_BINDINGS -> openBindingsPage(serverId)
            ITEM_LINKS -> openLinksPage(serverId)
            ITEM_SITEMAP_FILTER -> openSitemapSettingsPage(serverId)
        }

        if (fragment != null) {
            activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.page_container, fragment)
                    .addToBackStack(null)
                    .commit()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        serverId = arguments!!.getLong(ARG_SERVER)
        applicationComponent.inject(this)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        this.container = container
        val view = inflater.inflate(R.layout.fragment_servers_settings, container, false)

        unbinder = ButterKnife.bind(this, view)

        val items = ArrayList<ImageItem>()
        items.add(ImageItem(ITEM_EDIT, getString(R.string.edit), R.drawable.ic_edit))
        items.add(ImageItem(ITEM_INBOX, getString(R.string.inbox), R.drawable.ic_inbox))
        items.add(ImageItem(ITEM_BINDINGS, getString(R.string.bindings), R.drawable.ic_binding))
        items.add(ImageItem(ITEM_LINKS, getString(R.string.links), R.drawable.ic_link))
        items.add(ImageItem(ITEM_SITEMAP_FILTER, getString(R.string.sitemaps), R.drawable.ic_sitemap))
        adapter = ImageItemAdapter(R.layout.item_menu_image_box)

        // Set the adapter
        listView.adapter = adapter
        val layoutManager = GridLayoutManager(activity, 2)
        listView.layoutManager = layoutManager
        listView.itemAnimator = DefaultItemAnimator()
        adapter.setItemClickListener(optionsSelectListener)
        adapter.addAll(items)
        listView.adapter = adapter

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setTitle(R.string.settings)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder!!.unbind()
    }

    /**
     * Open inbox page
     * @param serverId the server to open page for.
     */
    private fun openInboxPage(serverId: Long) {
        activity!!.supportFragmentManager.beginTransaction()
                .replace(container!!.id, InboxListFragment.newInstance(serverId))
                .addToBackStack(null)
                .commit()
    }

    /**
     * Open bindings page for server.
     * @param serverId the server to open page for.
     */
    private fun openBindingsPage(serverId: Long) {
        activity!!.supportFragmentManager.beginTransaction()
                .replace(container!!.id, BindingsFragment.newInstance(serverId))
                .addToBackStack(null)
                .commit()
    }

    /**
     * Open sitemap page for server.
     * @param serverId the server to open page for.
     */
    private fun openSitemapSettingsPage(serverId: Long) {
        activity!!.supportFragmentManager.beginTransaction()
                .replace(container!!.id, SitemapSelectFragment.newInstance(serverId))
                .addToBackStack(null)
                .commit()
    }

    /**
     * Open links page for server.
     * @param serverId the server to open page for.
     */
    private fun openLinksPage(serverId: Long) {
        activity!!.supportFragmentManager.beginTransaction()
                .replace(container!!.id, LinksListFragment.newInstance(serverId))
                .addToBackStack(null)
                .commit()
    }

    companion object {

        private val ARG_SERVER = "arg_server"

        @IntDef(ITEM_EDIT.toLong(), ITEM_INBOX.toLong(), ITEM_LINKS.toLong(), ITEM_BINDINGS.toLong(), ITEM_SITEMAP_FILTER.toLong())
        @Retention(AnnotationRetention.SOURCE)
        annotation class ServerActions

        const val ITEM_EDIT = 1
        const val ITEM_INBOX = 2
        const val ITEM_BINDINGS = 3
        const val ITEM_SITEMAP_FILTER = 4
        const val ITEM_LINKS = 5

        fun newInstance(serverId: Long): ServerMenuFragment {
            val fragment = ServerMenuFragment()
            val args = Bundle()
            args.putLong(ARG_SERVER, serverId)
            fragment.arguments = args
            return fragment
        }
    }
}
