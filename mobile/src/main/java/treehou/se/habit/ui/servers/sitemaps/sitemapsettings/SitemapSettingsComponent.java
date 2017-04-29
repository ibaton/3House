package treehou.se.habit.ui.servers.sitemaps.sitemapsettings;


import dagger.Subcomponent;
import treehou.se.habit.module.ActivityScope;
import treehou.se.habit.module.FragmentComponent;
import treehou.se.habit.module.FragmentComponentBuilder;

@ActivityScope
@Subcomponent(
        modules = SitemapSettingsModule.class
)
public interface SitemapSettingsComponent extends FragmentComponent<SitemapSettingsFragment> {

    @Subcomponent.Builder
    interface Builder extends FragmentComponentBuilder<SitemapSettingsModule, SitemapSettingsComponent> {
    }
}