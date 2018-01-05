package treehou.se.habit.ui.colorpicker


import se.treehou.ng.ohcommunicator.connector.models.OHItem
import treehou.se.habit.mvp.BasePresenter
import treehou.se.habit.mvp.BaseView

class LightContract {

    interface View : BaseView<Presenter>

    interface Presenter : BasePresenter {
        fun setHSV(item: OHItem, hue: Int, saturation: Int, value: Int)
    }
}
