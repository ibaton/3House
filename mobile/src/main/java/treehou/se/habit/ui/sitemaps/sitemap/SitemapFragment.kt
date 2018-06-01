package treehou.se.habit.ui.sitemaps.sitemap

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import io.realm.Realm
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap
import se.treehou.ng.ohcommunicator.util.GsonHelper
import treehou.se.habit.R
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.dagger.HasActivitySubcomponentBuilders
import treehou.se.habit.dagger.fragment.SitemapComponent
import treehou.se.habit.dagger.fragment.SitemapModule
import treehou.se.habit.mvp.BaseDaggerFragment
import treehou.se.habit.service.VoiceService
import treehou.se.habit.ui.sitemaps.page.PageFragment
import treehou.se.habit.ui.sitemaps.sitemap.SitemapContract.Presenter
import javax.inject.Inject

class SitemapFragment : BaseDaggerFragment<Presenter>(), SitemapContract.View {

    @Inject lateinit var sitemapPresenter: Presenter
    @Inject @JvmField var server: ServerDB? = null
    @Inject @JvmField var sitemap: OHSitemap? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupActionbar()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_sitemap, container, false)

        setHasOptionsMenu(isVoiceCommandSupported(server))

        return rootView
    }

    /**
     * Setup actionbar using
     */
    private fun setupActionbar() {
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) actionBar.title = sitemap?.label
    }

    /**
     * Check if a page is loaded.
     * @return true if page loaded, else false.
     */
    override fun hasPage(): Boolean {
        return childFragmentManager.backStackEntryCount > 0
    }

    /**
     * Add and move to page in view pager.
     *
     * @param page the page to add to pager
     */
    override fun showPage(server: ServerDB, page: OHLinkedPage) {
        Log.d(TAG, "Add page " + page.link)
        childFragmentManager.beginTransaction()
                .replace(R.id.pgr_sitemap, PageFragment.newInstance(server, page))
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.sitemap, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // Handle presses on the action bar items
        when (item!!.itemId) {
            R.id.action_voice_command -> {
                openVoiceCommand(server)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Start voice command listener.
     *
     * @param server server to send command to.
     */
    fun openVoiceCommand(server: ServerDB?) {
        if (isVoiceCommandSupported(server)) {
            startActivity(createVoiceCommandIntent(server))
        }
    }

    /**
     * Creates an intent use to input voice command.
     * @return intent used to fire voice command
     */
    private fun createVoiceCommandIntent(server: ServerDB?): Intent {
        val openhabPendingIntent = VoiceService.createPendingVoiceCommand(activity!!, server!!, 9)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        // Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, VoiceService::class.java.`package`.name)
        // Display an hint to the user about what he should say.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_command_title))
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(RecognizerIntent.EXTRA_RESULTS_PENDINGINTENT, openhabPendingIntent)
        return intent
    }

    /**
     * Check if cvoice command is supported by device.
     * @return true if supported, else false
     */
    private fun isVoiceCommandSupported(server: ServerDB?): Boolean {
        val packageManager = context!!.packageManager
        return packageManager.resolveActivity(createVoiceCommandIntent(server), 0) != null
    }

    /**
     * Pop backstack
     * @return true if handled by fragment, else false.
     */
    override fun removeAllPages(): Boolean {
        val fragmentManager = childFragmentManager
        var backStackEntryCount = fragmentManager.backStackEntryCount
        if (backStackEntryCount > 0) {
            fragmentManager.popBackStackImmediate()
        }
        backStackEntryCount = fragmentManager.backStackEntryCount

        return backStackEntryCount >= 1
    }

    override fun getPresenter(): Presenter? {
        return sitemapPresenter
    }

    override fun injectMembers(hasActivitySubcomponentBuilders: HasActivitySubcomponentBuilders) {
        (hasActivitySubcomponentBuilders.getFragmentComponentBuilder(SitemapFragment::class.java) as SitemapComponent.Builder)
                .fragmentModule(SitemapModule(this, arguments!!))
                .build().injectMembers(this)
    }

    companion object {

        private val TAG = "SitemapFragment"

        /**
         * Creates a new instance of fragment showing sitemap.
         *
         * @param server the server to use to open sitemap.
         * @param sitemap the sitemap to load.
         * @return Fragment displaying sitemap.
         */
        fun newInstance(server: OHServer, sitemap: OHSitemap): SitemapFragment {
            val serverDB = Realm.getDefaultInstance()
                    .where(ServerDB::class.java)
                    .equalTo("name", server.name)
                    .findFirst()

            return newInstance(serverDB, sitemap)
        }

        /**
         * Creates a new instance of fragment showing sitemap.,
         *
         * @param serverDB the server to use to open sitemap.
         * @param sitemap the sitemap to load.
         * @return Fragment displaying sitemap.
         */
        fun newInstance(serverDB: ServerDB?, sitemap: OHSitemap): SitemapFragment {
            val fragment = SitemapFragment()

            val args = Bundle()
            args.putString(Presenter.ARG_SITEMAP, GsonHelper.createGsonBuilder().toJson(sitemap))
            args.putLong(Presenter.ARG_SERVER, serverDB!!.id)
            fragment.arguments = args

            return fragment
        }
    }
}
