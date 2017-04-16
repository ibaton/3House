package treehou.se.habit.ui.settings.subsettings.general;

import treehou.se.habit.mvp.BasePresenter;
import treehou.se.habit.mvp.BaseView;

public interface GeneralSettingsContract {

    interface View extends BaseView<Presenter> {
        void updateTheme();
        void showAutoLoadSitemap(boolean show);
        void showSitemapsInMenu(Boolean show);
        void setFullscreen(boolean fullscreaan);
    }

    interface Presenter extends BasePresenter {
        void themeSelected(int theme);
        void setAutoLoadSitemap(boolean show);
        void setFullscreen(boolean fullscreaan);
        void setShowSitemapsInMenu(Boolean show);
    }
}
