package treehou.se.habit.ui.sitemaps.sitemaplist;


import dagger.Subcomponent;
import treehou.se.habit.module.ActivityScope;
import treehou.se.habit.module.FragmentComponent;
import treehou.se.habit.module.FragmentComponentBuilder;

@ActivityScope
@Subcomponent(
        modules = SitemapListModule.class
)
public interface SitemapListComponent extends FragmentComponent<SitemapListFragment> {

    @Subcomponent.Builder
    interface Builder extends FragmentComponentBuilder<SitemapListModule, SitemapListComponent> {

    }
}