package treehou.se.habit.ui.sitemaps.page

import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_widget_list.*
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHWidget
import se.treehou.ng.ohcommunicator.util.GsonHelper
import treehou.se.habit.R
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.dagger.HasActivitySubcomponentBuilders
import treehou.se.habit.dagger.fragment.PageComponent
import treehou.se.habit.dagger.fragment.PageModule
import treehou.se.habit.mvp.BaseDaggerFragment
import treehou.se.habit.ui.adapter.WidgetAdapter
import javax.inject.Inject
import android.support.v7.widget.DividerItemDecoration



class PageFragment : BaseDaggerFragment<PageContract.Presenter>(), PageContract.View {

    @Inject lateinit var pageFragmentPresenter: PageContract.Presenter
    @Inject lateinit var adapter: WidgetAdapter

    override fun showLostServerConnectionMessage() {
        Toast.makeText(activity, R.string.lost_server_connection, Toast.LENGTH_LONG).show()
    }

    override fun closeView() {
        activity!!.supportFragmentManager.popBackStack()
    }

    override fun getPresenter(): PageContract.Presenter? {
        return pageFragmentPresenter
    }

    override fun injectMembers(hasActivitySubcomponentBuilders: HasActivitySubcomponentBuilders) {
        (hasActivitySubcomponentBuilders.getFragmentComponentBuilder(PageFragment::class.java) as PageComponent.Builder)
                .fragmentModule(PageModule(this, arguments!!))
                .build().injectMembers(this)
    }

    override fun updatePage(page: OHLinkedPage) {
        setupActionbar(page)
    }

    override fun setWidgets(widgets: List<OHWidget>) {
        adapter.setWidgets(widgets)
    }

    /**
     * Setup actionbar using
     */
    private fun setupActionbar(page: OHLinkedPage) {
        var actionBar: ActionBar? = null
        if (isAdded) {
            actionBar = (activity as AppCompatActivity).supportActionBar
        }
        var title: String? = page.title
        if (title == null) title = ""
        title = removeValueFromTitle(title)

        if (actionBar != null) actionBar.title = title
    }

    private fun removeValueFromTitle(title: String): String {
        return title.replace("\\[.+?\\]".toRegex(), "")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_widget_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = context

        val linearLayoutManager = LinearLayoutManager(context)
        widgetList.layoutManager = linearLayoutManager
        widgetList.itemAnimator = DefaultItemAnimator()
        val dividerItemDecoration = DividerItemDecoration(widgetList.context, linearLayoutManager.orientation)
        if(context != null) {
            val divider = ResourcesCompat.getDrawable(context.resources, R.drawable.item_divider, context.theme)
            divider?.let { dividerItemDecoration.setDrawable(it) }
        }
        widgetList.addItemDecoration(dividerItemDecoration)
        widgetList.adapter = adapter
    }

    companion object {

        private val TAG = "PageFragment"

        /**
         * Creates a new instane of the page.
         *
         * @param server the server to connect to
         * @param page the page to visualise
         *
         * @return Fragment visualazing a page
         */
        fun newInstance(server: ServerDB, page: OHLinkedPage): PageFragment {
            val gson = GsonHelper.createGsonBuilder()

            val args = Bundle()
            args.putString(PageContract.ARG_PAGE, gson.toJson(page))
            args.putLong(PageContract.ARG_SERVER, server.id)

            val fragment = PageFragment()
            fragment.arguments = args

            return fragment
        }
    }
}
