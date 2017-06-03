package treehou.se.habit.ui.colorpicker;


import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import treehou.se.habit.mvp.BasePresenter;
import treehou.se.habit.mvp.BaseView;

public class LightContract {

    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter {
        void setHSV(OHItem item, int hue, int saturation, int value);
    }
}
