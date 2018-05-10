package treehou.se.habit.ui.settings

import treehou.se.habit.R
import treehou.se.habit.mvp.BasePresenter
import treehou.se.habit.mvp.BaseView

interface SettingsContract {

    interface View : BaseView<Presenter> {
        fun showGeneralSettings()
        fun showLicense()
        fun showTranslatePage()
    }

    interface Presenter : BasePresenter {
        fun openGeneralSettings()
        fun openLicense()
        fun openTranslatePage()
    }
}
