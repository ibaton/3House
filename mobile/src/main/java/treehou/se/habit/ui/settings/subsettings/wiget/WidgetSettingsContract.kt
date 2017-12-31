package treehou.se.habit.ui.settings.subsettings.wiget

import treehou.se.habit.mvp.BasePresenter
import treehou.se.habit.mvp.BaseView

interface WidgetSettingsContract {

    interface View : BaseView<Presenter> {
        fun setWidgetBackground(backgroundType: Int)
        fun setWidgetTextSize(size: Int)
        fun setWidgetImageSize(size: Int)
        fun setCompressedWidgetButton(isChecked: Boolean)
        fun setCompressedWidgetSlider(isChecked: Boolean)
    }

    interface Presenter : BasePresenter {
        fun setWidgetBackground(backgroundType: Int)
        fun setWidgetTextSize(size: Int)
        fun setWidgetImageSize(size: Int)
        fun setCompressedWidgetButton(isChecked: Boolean)
        fun setCompressedWidgetSlider(isChecked: Boolean)
    }
}
