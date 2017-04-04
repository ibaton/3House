package treehou.se.habit.sitemaps;


import dagger.Subcomponent;
import treehou.se.habit.main.MainActivityModule;
import treehou.se.habit.module.ActivityScope;
import treehou.se.habit.module.FragmentComponent;
import treehou.se.habit.module.FragmentComponentBuilder;
import treehou.se.habit.ui.sitemaps.SitemapListFragment;

@ActivityScope
@Subcomponent(
        modules = SitemapListModule.class
)
public interface SitemapListComponent extends FragmentComponent<SitemapListFragment> {

    @Subcomponent.Builder
    interface Builder extends FragmentComponentBuilder<SitemapListModule, SitemapListComponent> {

    }
}