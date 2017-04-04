package treehou.se.habit.ui.settings;


import dagger.Subcomponent;
import treehou.se.habit.module.ActivityScope;
import treehou.se.habit.module.FragmentComponent;
import treehou.se.habit.module.FragmentComponentBuilder;
import treehou.se.habit.sitemaps.SitemapListModule;
import treehou.se.habit.ui.sitemaps.SitemapListFragment;

@ActivityScope
@Subcomponent(
        modules = SettingsModule.class
)
public interface SettingsComponent extends FragmentComponent<SettingsFragment> {

    @Subcomponent.Builder
    interface Builder extends FragmentComponentBuilder<SettingsModule, SettingsComponent> {
    }
}