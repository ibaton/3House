package treehou.se.habit.ui.settings.subsettings.general;

import android.os.Bundle;

import javax.inject.Inject;

import treehou.se.habit.module.RxPresenter;

public class GeneralSettingsPresenter extends RxPresenter implements GeneralSettingsContract.Presenter {

    private GeneralSettingsContract.View view;

    @Inject
    public GeneralSettingsPresenter(GeneralSettingsContract.View view) {
        this.view = view;
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
