package treehou.se.habit.ui.settings.subsettings.general


import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter

import com.jakewharton.rxbinding2.widget.RxCompoundButton

import javax.inject.Inject

import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_settings_general.*
import treehou.se.habit.R
import treehou.se.habit.dagger.HasActivitySubcomponentBuilders
import treehou.se.habit.dagger.fragment.GeneralSettingsComponent
import treehou.se.habit.dagger.fragment.GeneralSettingsModule
import treehou.se.habit.mvp.BaseDaggerFragment
import treehou.se.habit.util.Settings

class GeneralSettingsFragment : BaseDaggerFragment<GeneralSettingsContract.Presenter>(), GeneralSettingsContract.View {

    @Inject lateinit var settingsPresenter: GeneralSettingsContract.Presenter
    @Inject lateinit var settings: Settings
    @Inject lateinit var themes: Array<ThemeItem>

    override fun getPresenter(): GeneralSettingsContract.Presenter? {
        return settingsPresenter
    }

    override fun injectMembers(hasActivitySubcomponentBuilders: HasActivitySubcomponentBuilders) {
        (hasActivitySubcomponentBuilders.getFragmentComponentBuilder(GeneralSettingsFragment::class.java) as GeneralSettingsComponent.Builder)
                .fragmentModule(GeneralSettingsModule(this))
                .build().injectMembers(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings_general, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val actionBar = (activity as AppCompatActivity).supportActionBar

        actionBar?.setTitle(R.string.settings_general)

        cbxFullscreen.visibility = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) View.VISIBLE else View.GONE

        val themeAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, themes)
        spinnerThemes.adapter = themeAdapter

        val themeListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                settingsPresenter.themeSelected(themes[position].theme)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

        settings.themeRx
                .subscribeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe ({
                    for (i in themes.indices) {
                        if (themes[i].theme == settings.theme) {
                            spinnerThemes.onItemSelectedListener = null
                            spinnerThemes.setSelection(i)
                            break
                        }
                    }
                    spinnerThemes.onItemSelectedListener = themeListener
                }, {logger.e(TAG, "Failed to load theme", it)})

        RxCompoundButton.checkedChanges(cbxShowSitemapInMenu)
                .compose(bindToLifecycle())
                .skip(1)
                .subscribe ({ show -> settingsPresenter.setShowSitemapsInMenu(show) }
                        , {logger.e(TAG, "cbxShowSitemapInMenu update failed", it)})

        RxCompoundButton.checkedChanges(cbxAutoLoadSitemap)
                .compose(bindToLifecycle())
                .skip(1)
                .subscribe ({ show -> settingsPresenter.setAutoLoadSitemap(show!!) }
                        , {logger.e(TAG, "cbxAutoLoadSitemap update failed", it)})

        RxCompoundButton.checkedChanges(cbxFullscreen)
                .compose(bindToLifecycle())
                .skip(1)
                .subscribe ({ show -> settingsPresenter.setFullscreen(show!!) }
                        , {logger.e(TAG, "cbxFullscreen update failed", it)})
    }

    override fun updateTheme() {
        activity!!.recreate()
    }

    override fun showAutoLoadSitemap(show: Boolean) {
        cbxAutoLoadSitemap.isChecked = show
    }

    override fun showSitemapsInMenu(show: Boolean?) {

        cbxShowSitemapInMenu.isChecked = show!!
    }

    override fun setFullscreen(fullscreen: Boolean) {
        cbxFullscreen.isChecked = fullscreen
    }

    companion object {

        private val TAG = GeneralSettingsFragment::class.java.simpleName

        fun newInstance(): GeneralSettingsFragment {
            val fragment = GeneralSettingsFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
