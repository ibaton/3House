package treehou.se.habit.ui.settings.subsettings.wiget;

import treehou.se.habit.mvp.BasePresenter;
import treehou.se.habit.mvp.BaseView;

public interface WidgetSettingsContract {

    interface View extends BaseView<Presenter> {
        void setWidgetBackground(int backgroundType);
        void setWidgetTextSize(int size);
        void setWidgetImageSize(int size);
        void setCompressedWidgetButton(boolean isChecked);
        void setCompressedWidgetSlider(boolean isChecked);
    }

    interface Presenter extends BasePresenter {
        void setWidgetBackground(int backgroundType);
        void setWidgetTextSize(int size);
        void setWidgetImageSize(int size);
        void setCompressedWidgetButton(boolean isChecked);
        void setCompressedWidgetSlider(boolean isChecked);
    }
}
