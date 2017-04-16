package treehou.se.habit.ui.settings.subsettings.general;

import android.os.Bundle;

import com.f2prateek.rx.preferences.Preference;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.trello.rxlifecycle.RxLifecycle;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import treehou.se.habit.module.RxPresenter;
import treehou.se.habit.util.Settings;

public class GeneralSettingsPresenter extends RxPresenter implements GeneralSettingsContract.Presenter {

    @Inject Settings settings;

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

        Preference<Boolean> settingsAutoloadSitemapRx = settings.getAutoloadSitemapRx();
        settingsAutoloadSitemapRx.asObservable()
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(show -> view.showAutoLoadSitemap(show));

        Preference<Boolean> settingsShowSitemapInMenuRx = settings.getShowSitemapsInMenuRx();
        settingsShowSitemapInMenuRx.asObservable()
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(show -> view.showSitemapsInMenu(show));

        Preference<Boolean> settingsFullscreenRx = settings.getFullscreenPref();
        settingsFullscreenRx.asObservable()
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(show -> view.setFullscreen(show));
    }

    @Override
    public void themeSelected(int theme) {
        Preference<Integer> themePref = settings.getThemePref();
        if(themePref.get() != theme) {
            themePref.set(theme);
            view.updateTheme();
        }
    }

    @Override
    public void setShowSitemapsInMenu(Boolean show) {
        Preference<Boolean> showSitemapsInMenuRx = settings.getShowSitemapsInMenuRx();
        if(show != showSitemapsInMenuRx.get()) {
            showSitemapsInMenuRx.set(show);
        }
    }

    @Override
    public void setAutoLoadSitemap(boolean show) {
        Preference<Boolean> autoloadSitemapRx = settings.getAutoloadSitemapRx();
        if(show != autoloadSitemapRx.get()) {
            autoloadSitemapRx.set(show);
        }
    }

    @Override
    public void setFullscreen(boolean fullscreen) {
        Preference<Boolean> fullscreenPrefRx = settings.getFullscreenPref();
        if(fullscreen != fullscreenPrefRx.get()) {
            fullscreenPrefRx.set(fullscreen);
        }
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
