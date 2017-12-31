package treehou.se.habit.ui.settings

import android.os.Bundle

import javax.inject.Inject

import treehou.se.habit.module.RxPresenter

class SettingsPresenter @Inject
constructor(private val view: SettingsContract.View) : RxPresenter(), SettingsContract.Presenter {

    override fun openWidgetSettings() {
        view.showWidgetSettings()
    }

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
