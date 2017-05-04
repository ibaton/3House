package treehou.se.habit.ui.settings;

import android.os.Bundle;

import javax.inject.Inject;

import treehou.se.habit.module.RxPresenter;

public class SettingsPresenter extends RxPresenter implements SettingsContract.Presenter {

    private SettingsContract.View view;

    @Inject
    public SettingsPresenter(SettingsContract.View view) {
        this.view = view;
    }

    @Override
    public void openWidgetSettings() {
        view.showWidgetSettings();
    }

    @Override
    public void openGeneralSettings() {
        view.showGeneralSettings();
    }

    @Override
    public void openLicense() {
        view.showLicense();
    }

    @Override
    public void openTranslatePage() {
        view.showTranslatePage();
    }

    @Override
    public void load(Bundle savedData) {
    }

    @Override
    public void subscribe() {
    }

    @Override
    public void unsubscribe() {
    }

    @Override
    public void unload() {
    }

    @Override
    public void save(Bundle savedData) {}
}
