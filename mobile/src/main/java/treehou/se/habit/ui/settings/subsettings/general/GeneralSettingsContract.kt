package treehou.se.habit.ui.settings.subsettings.general

import treehou.se.habit.mvp.BasePresenter
import treehou.se.habit.mvp.BaseView

interface GeneralSettingsContract {

    interface View : BaseView<Presenter> {
        fun updateTheme()
        fun showAutoLoadSitemap(show: Boolean)
        fun showSitemapsInMenu(show: Boolean?)
        fun setFullscreen(fullscreen: Boolean)
    }

    interface Presenter : BasePresenter {
        fun themeSelected(theme: Int)
        fun setAutoLoadSitemap(show: Boolean)
        fun setFullscreen(fullscreen: Boolean)
        fun setShowSitemapsInMenu(show: Boolean)
    }
}
