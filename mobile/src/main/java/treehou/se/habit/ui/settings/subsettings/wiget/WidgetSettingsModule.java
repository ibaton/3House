package treehou.se.habit.ui.settings.subsettings.wiget;


import dagger.Module;
import dagger.Provides;
import treehou.se.habit.module.ViewModule;
import treehou.se.habit.ui.settings.subsettings.general.GeneralSettingsContract;
import treehou.se.habit.ui.settings.subsettings.general.GeneralSettingsFragment;
import treehou.se.habit.ui.settings.subsettings.general.GeneralSettingsPresenter;

@Module
public class WidgetSettingsModule extends ViewModule<WidgetSettingsFragment> {

    public WidgetSettingsModule(WidgetSettingsFragment fragment) {
        super(fragment);
    }

    @Provides
    public WidgetSettingsContract.View provideView() {
        return view;
    }

    @Provides
    public WidgetSettingsContract.Presenter providePresenter(WidgetSettingsPresenter presenter) {
        return presenter;
    }
}
