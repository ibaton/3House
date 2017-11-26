package treehou.se.habit.ui.settings.subsettings.general;

import com.f2prateek.rx.preferences2.Preference;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
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
    public void subscribe() {
        super.subscribe();

        Observable<Boolean> settingsAutoloadSitemapRx = settings.getAutoloadSitemapRx();
        settingsAutoloadSitemapRx
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
        Observable<Boolean> autoloadSitemapRx = settings.getAutoloadSitemapRx();
        if(show != autoloadSitemapRx.blockingFirst()) {
            settings.setAutoloadSitemapRx(show);
        }
    }

    @Override
    public void setFullscreen(boolean fullscreen) {
        Preference<Boolean> fullscreenPrefRx = settings.getFullscreenPref();
        if(fullscreen != fullscreenPrefRx.get()) {
            fullscreenPrefRx.set(fullscreen);
        }
    }
}
