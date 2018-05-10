package treehou.se.habit.ui.settings

import javax.inject.Inject

import treehou.se.habit.dagger.RxPresenter

class SettingsPresenter @Inject
constructor(private val view: SettingsContract.View) : RxPresenter(), SettingsContract.Presenter {

    override fun openGeneralSettings() {
        view.showGeneralSettings()
    }

    override fun openLicense() {
        view.showLicense()
    }

    override fun openTranslatePage() {
        view.showTranslatePage()
    }
}
