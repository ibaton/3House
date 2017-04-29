package treehou.se.habit.ui.servers.sitemaps.list;


import dagger.Subcomponent;
import treehou.se.habit.module.ActivityScope;
import treehou.se.habit.module.FragmentComponent;
import treehou.se.habit.module.FragmentComponentBuilder;
import treehou.se.habit.ui.servers.serverlist.ServersFragment;
import treehou.se.habit.ui.servers.serverlist.ServersModule;

@ActivityScope
@Subcomponent(
        modules = SitemapSelectModule.class
)
public interface SitemapSelectComponent extends FragmentComponent<SitemapSelectFragment> {

    @Subcomponent.Builder
    interface Builder extends FragmentComponentBuilder<SitemapSelectModule, SitemapSelectComponent> {
    }
}