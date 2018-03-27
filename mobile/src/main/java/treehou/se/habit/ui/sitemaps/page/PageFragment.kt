package treehou.se.habit.ui.sitemaps.page

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.util.GsonHelper
import treehou.se.habit.R
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.dagger.HasActivitySubcomponentBuilders
import treehou.se.habit.dagger.fragment.PageComponent
import treehou.se.habit.dagger.fragment.PageModule
import treehou.se.habit.mvp.BaseDaggerFragment
import treehou.se.habit.ui.widgets.WidgetFactory
import javax.inject.Inject

class PageFragment : BaseDaggerFragment<PageContract.Presenter>(), PageContract.View {

    @BindView(R.id.lou_widgets) lateinit var louWidgets: LinearLayout

    @Inject lateinit var pageFragmentPresenter: PageContract.Presenter

    private var unbinder: Unbinder? = null

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

    private fun removeAllWidgets() {
        louWidgets.removeAllViews()
    }

    override fun setWidgets(widgets: List<WidgetFactory.IWidgetHolder>) {
        removeAllWidgets()

        for (widget in widgets) {
            louWidgets.addView(widget.view)
        }
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
        val view = inflater.inflate(R.layout.fragment_widget, container, false)
        unbinder = ButterKnife.bind(this, view)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder!!.unbind()
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
