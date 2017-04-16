package treehou.se.habit.ui.settings.subsettings.wiget;

import javax.inject.Inject;

import io.realm.Realm;
import treehou.se.habit.core.db.settings.WidgetSettingsDB;
import treehou.se.habit.module.RxPresenter;

public class WidgetSettingsPresenter extends RxPresenter implements WidgetSettingsContract.Presenter {

    private WidgetSettingsContract.View view;
    private Realm realm;

    @Inject
    public WidgetSettingsPresenter(WidgetSettingsContract.View view, Realm realm) {
        this.view = view;
        this.realm = realm;
    }

    @Override
    public void setWidgetBackground(int backgroundType) {
        WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(realm);
        realm.beginTransaction();
        settings.setImageBackground(backgroundType);
        realm.commitTransaction();
        view.setWidgetBackground(backgroundType);
    }

    @Override
    public void setWidgetTextSize(int size) {
        final WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(realm);
        realm.beginTransaction();
        settings.setTextSize(size);
        realm.commitTransaction();
        view.setWidgetTextSize(size);
    }

    @Override
    public void setWidgetImageSize(int size) {
        final WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(realm);
        realm.beginTransaction();
        settings.setIconSize(size);
        realm.commitTransaction();
        view.setWidgetImageSize(size);
    }

    @Override
    public void setCompressedWidgetSlider(boolean isChecked) {
        WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(realm);
        realm.beginTransaction();
        settings.setCompressedSlider(isChecked);
        realm.commitTransaction();
        view.setCompressedWidgetSlider(isChecked);
    }

    @Override
    public void setCompressedWidgetButton(boolean isChecked) {
        WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(realm);
        realm.beginTransaction();
        settings.setCompressedSingleButton(isChecked);
        realm.commitTransaction();
        view.setCompressedWidgetButton(isChecked);
    }
}
