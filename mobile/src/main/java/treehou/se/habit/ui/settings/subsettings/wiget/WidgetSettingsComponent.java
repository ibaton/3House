package treehou.se.habit.ui.settings.subsettings.wiget;


import dagger.Subcomponent;
import treehou.se.habit.module.ActivityScope;
import treehou.se.habit.module.FragmentComponent;
import treehou.se.habit.module.FragmentComponentBuilder;
import treehou.se.habit.ui.settings.subsettings.general.GeneralSettingsFragment;
import treehou.se.habit.ui.settings.subsettings.general.GeneralSettingsModule;

@ActivityScope
@Subcomponent(
        modules = WidgetSettingsModule.class
)
public interface WidgetSettingsComponent extends FragmentComponent<WidgetSettingsFragment> {

    @Subcomponent.Builder
    interface Builder extends FragmentComponentBuilder<WidgetSettingsModule, WidgetSettingsComponent> {
    }
}