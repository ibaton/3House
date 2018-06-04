package treehou.se.habit.ui.servers.sitemaps.sitemapsettings

import android.os.Bundle
import android.support.v4.util.Pair
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible

import com.jakewharton.rxbinding2.widget.RxCompoundButton

import javax.inject.Inject

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.fragment_sitemap_settings.*
import treehou.se.habit.R
import treehou.se.habit.core.db.model.SitemapDB
import treehou.se.habit.dagger.HasActivitySubcomponentBuilders
import treehou.se.habit.dagger.fragment.SitemapSettingsComponent
import treehou.se.habit.dagger.fragment.SitemapSettingsModule
import treehou.se.habit.mvp.BaseDaggerFragment

/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class SitemapSettingsFragment : BaseDaggerFragment<SitemapSettingsContract.Presenter>(), SitemapSettingsContract.View {

    @Inject lateinit var settingsPresenter: SitemapSettingsContract.Presenter

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
        return inflater.inflate(R.layout.fragment_sitemap_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar()

        val sitemapObservable = realm.where(SitemapDB::class.java).equalTo("id", sitemapId).findAll()
                .asFlowable()
                .flatMap { Flowable.fromIterable(it) }
                .filter { sitemapDB -> sitemapDB.settingsDB != null }
                .distinctUntilChanged()

        sitemapObservable.map { sitemapDB -> sitemapDB.settingsDB!!.display }
                .first(true)
                .compose(bindToLifecycle())
                .subscribe({
                    cbxShowSitemaps.isChecked = it
                    cbxShowSitemaps.isVisible = true
                    setupListener(sitemapObservable)
                }, {logger.e(TAG, "Sitemap observable failed", it)})
    }

    private fun setupListener(sitemapObservable: Flowable<SitemapDB>) {
        Observable.combineLatest<SitemapDB, Boolean, Pair<SitemapDB, Boolean>>(sitemapObservable.toObservable(),
                RxCompoundButton.checkedChanges(cbxShowSitemaps)
                        .doOnNext { Log.d("Yolo", "Here3 " + it) },
                BiFunction<SitemapDB, Boolean, Pair<SitemapDB, Boolean>> { first, second ->
                    Log.d("Yolo", "Here3.5 " + second)
                    Pair(first, second)
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { Log.d("Yolo", "Here4 " + it.second) }
                .subscribe ({
                    val sitemapDB = it.first
                    val showSitemap = it.second!!
                    realm.beginTransaction()
                    sitemapDB!!.settingsDB!!.display = showSitemap
                    realm.commitTransaction()
                }, {logger.e(TAG, "Sitemap db observable failed", it)})

    }

    /**
     * Setup actionbar.
     */
    private fun setupActionBar() {
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setTitle(R.string.sitemaps)
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
