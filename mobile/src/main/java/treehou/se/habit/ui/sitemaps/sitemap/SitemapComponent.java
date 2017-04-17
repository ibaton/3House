package treehou.se.habit.ui.sitemaps.sitemap;


import dagger.Subcomponent;
import treehou.se.habit.module.ActivityScope;
import treehou.se.habit.module.FragmentComponent;
import treehou.se.habit.module.FragmentComponentBuilder;

@ActivityScope
@Subcomponent(
        modules = SitemapModule.class
)
public interface SitemapComponent extends FragmentComponent<SitemapFragment> {

    @Subcomponent.Builder
    interface Builder extends FragmentComponentBuilder<SitemapModule, SitemapComponent> {

    }
}