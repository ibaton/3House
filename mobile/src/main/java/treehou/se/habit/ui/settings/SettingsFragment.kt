package treehou.se.habit.ui.settings

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ListAdapter
import com.mikepenz.aboutlibraries.LibsBuilder
import treehou.se.habit.R
import treehou.se.habit.dagger.HasActivitySubcomponentBuilders
import treehou.se.habit.dagger.fragment.SettingsComponent
import treehou.se.habit.dagger.fragment.SettingsModule
import treehou.se.habit.mvp.BaseDaggerFragment
import treehou.se.habit.ui.adapter.ImageAdapter
import treehou.se.habit.ui.adapter.ImageItem
import treehou.se.habit.ui.settings.subsettings.general.GeneralSettingsFragment
import treehou.se.habit.util.IntentHelper
import java.util.*
import javax.inject.Inject

/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class SettingsFragment : BaseDaggerFragment<SettingsContract.Presenter>(), SettingsContract.View {

    private var actionBar: ActionBar? = null

    @Inject lateinit var settingsPresenter: SettingsContract.Presenter

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private var mAdapter: ListAdapter? = null

    internal var optionsSelectListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        val item = parent.getItemAtPosition(position) as ImageItem

        when (item.id) {
            ITEM_GENERAL -> settingsPresenter.openGeneralSettings()
            ITEM_LICENSES -> settingsPresenter.openLicense()
            ITEM_TRANSLATE -> settingsPresenter.openTranslatePage()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val items = ArrayList<ImageItem>()
        items.add(ImageItem(ITEM_GENERAL, getString(R.string.settings_general), R.drawable.ic_item_notification))
        items.add(ImageItem(ITEM_LICENSES, getString(R.string.open_source_libraries), R.drawable.ic_license))
        items.add(ImageItem(ITEM_TRANSLATE, getString(R.string.help_translate), R.drawable.ic_language))

        mAdapter = ImageAdapter(activity!!, items)
    }

    override fun showGeneralSettings() {
        val fragment = GeneralSettingsFragment.newInstance()
        openPage(fragment)
    }

    override fun showLicense() {
        actionBar!!.setTitle(R.string.open_source_libraries)
        val fragment = LibsBuilder().supportFragment()
        openPage(fragment)
    }

    override fun showTranslatePage() {
        openTranslationSite()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        actionBar = (activity as AppCompatActivity).supportActionBar

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Set the adapter

        val listView: AbsListView = view.findViewById(R.id.listView)
        listView.adapter = mAdapter
        listView.setOnItemClickListener(optionsSelectListener)
        actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) actionBar!!.setTitle(R.string.settings)

    }

    private fun openPage(fragment: Fragment?) {
        if (fragment != null) {
            activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.page_container, fragment)
                    .addToBackStack(null)
                    .commit()
        }
    }

    override fun getPresenter(): SettingsContract.Presenter? {
        return settingsPresenter
    }

    override fun injectMembers(hasActivitySubcomponentBuilders: HasActivitySubcomponentBuilders) {
        (hasActivitySubcomponentBuilders.getFragmentComponentBuilder(SettingsFragment::class.java) as SettingsComponent.Builder)
                .fragmentModule(SettingsModule(this))
                .build().injectMembers(this)
    }

    /**
     * Opens translation site for project.
     */
    private fun openTranslationSite() {
        startActivity(IntentHelper.helpTranslateIntent())
    }

    companion object {

        val ITEM_WIDGETS = 1
        val ITEM_GENERAL = 2
        val ITEM_CUSTOM_WIDGETS = 3
        val ITEM_LICENSES = 4
        val ITEM_TRANSLATE = 5

        fun newInstance(): SettingsFragment {
            val fragment = SettingsFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
