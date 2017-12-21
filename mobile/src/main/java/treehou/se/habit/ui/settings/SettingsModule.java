package treehou.se.habit.ui.settings;


import dagger.Module;
import dagger.Provides;
import treehou.se.habit.module.ViewModule;

@Module
public class SettingsModule extends ViewModule<SettingsFragment> {

    public SettingsModule(SettingsFragment fragment) {
        super(fragment);
    }

    @Provides
    public SettingsContract.View provideView() {
        return getView();
    }

    @Provides
    public SettingsContract.Presenter providePresenter(SettingsPresenter presenter) {
        return presenter;
    }
}
