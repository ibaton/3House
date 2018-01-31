package treehou.se.habit.ui.servers.sitemaps.sitemapsettings

import android.os.Bundle
import android.support.v4.util.Pair
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch

import com.jakewharton.rxbinding2.widget.RxCompoundButton

import javax.inject.Inject

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import treehou.se.habit.R
import treehou.se.habit.core.db.model.SitemapDB
import treehou.se.habit.module.HasActivitySubcomponentBuilders
import treehou.se.habit.mvp.BaseDaggerFragment

/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class SitemapSettingsFragment : BaseDaggerFragment<SitemapSettingsContract.Presenter>(), SitemapSettingsContract.View {

    @BindView(R.id.show_in_sitemap) lateinit var cbxShowSitemaps: Switch

    @Inject lateinit var settingsPresenter: SitemapSettingsContract.Presenter

    private var unbinder: Unbinder? = null
    private var sitemapId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sitemapId = arguments!!.getLong(ARG_SITEMAP)
    }

    override fun getPresenter(): SitemapSettingsContract.Presenter? {
        return settingsPresenter
    }

    override fun injectMembers(hasActivitySubcomponentBuilders: HasActivitySubcomponentBuilders) {
        (hasActivitySubcomponentBuilders.getFragmentComponentBuilder(SitemapSettingsFragment::class.java) as SitemapSettingsComponent.Builder)
                .fragmentModule(SitemapSettingsModule(this))
                .build().injectMembers(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sitemap_settings, container, false)
        unbinder = ButterKnife.bind(this, view)
        setupActionBar()

        val sitemapObservable = realm.where(SitemapDB::class.java).equalTo("id", sitemapId).findAll()
                .asFlowable()
                .flatMap { Flowable.fromIterable(it) }
                .filter { sitemapDB -> sitemapDB.settingsDB != null }
                .distinctUntilChanged()

        sitemapObservable.map { sitemapDB -> sitemapDB.settingsDB!!.display }
                .compose(bindToLifecycle())
                .subscribe(RxCompoundButton.checked(cbxShowSitemaps))

        Flowable.combineLatest<SitemapDB, Boolean, Pair<SitemapDB, Boolean>>(sitemapObservable,
                RxCompoundButton.checkedChanges(cbxShowSitemaps).toFlowable(BackpressureStrategy.LATEST),
                BiFunction<SitemapDB, Boolean, Pair<SitemapDB, Boolean>> { first, second -> Pair(first, second) })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { sitemapDBBooleanPair ->
                    val sitemapDB = sitemapDBBooleanPair.first
                    val showSitemap = sitemapDBBooleanPair.second!!
                    realm.beginTransaction()
                    sitemapDB!!.settingsDB!!.display = showSitemap
                    realm.commitTransaction()
                }

        return view
    }

    /**
     * Setup actionbar.
     */
    private fun setupActionBar() {
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setTitle(R.string.sitemaps)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder!!.unbind()
    }

    companion object {

        private val TAG = "SitemapSelectFragment"

        private val ARG_SITEMAP = "ARG_SITEMAP"

        /**
         * Load sitemaps setting.
         *
         * @param sitemapId the sitemap to load
         * @return Fragment
         */
        fun newInstance(sitemapId: Long): SitemapSettingsFragment {
            val fragment = SitemapSettingsFragment()
            val args = Bundle()
            args.putLong(ARG_SITEMAP, sitemapId)
            fragment.arguments = args
            return fragment
        }
    }
}
