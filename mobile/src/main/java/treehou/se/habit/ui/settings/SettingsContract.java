package treehou.se.habit.ui.settings;

import treehou.se.habit.R;
import treehou.se.habit.mvp.BasePresenter;
import treehou.se.habit.mvp.BaseView;

public interface SettingsContract {

    interface View extends BaseView<Presenter> {
        void showWidgetSettings();
        void showGeneralSettings();
        void showLicense();
        void showTranslatePage();
    }

    interface Presenter extends BasePresenter {
        void openWidgetSettings();
        void openGeneralSettings();
        void openLicense();
        void openTranslatePage();
    }
}
