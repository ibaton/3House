package treehou.se.habit.ui.settings.subsettings.general;


import dagger.Module;
import dagger.Provides;
import treehou.se.habit.module.ViewModule;

@Module
public class GeneralSettingsModule extends ViewModule<GeneralSettingsFragment> {

    public GeneralSettingsModule(GeneralSettingsFragment fragment) {
        super(fragment);
    }

    @Provides
    public GeneralSettingsContract.View provideView() {
        return getView();
    }

    @Provides
    public GeneralSettingsContract.Presenter providePresenter(GeneralSettingsPresenter presenter) {
        return presenter;
    }
}
