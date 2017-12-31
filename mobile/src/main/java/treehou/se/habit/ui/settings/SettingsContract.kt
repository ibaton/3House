package treehou.se.habit.ui.settings

import treehou.se.habit.R
import treehou.se.habit.mvp.BasePresenter
import treehou.se.habit.mvp.BaseView

interface SettingsContract {

    interface View : BaseView<Presenter> {
        fun showWidgetSettings()
        fun showGeneralSettings()
        fun showLicense()
        fun showTranslatePage()
    }

    interface Presenter : BasePresenter {
        fun openWidgetSettings()
        fun openGeneralSettings()
        fun openLicense()
        fun openTranslatePage()
    }
}
