package treehou.se.habit.ui.sitemaps.sitemaplist


import dagger.Subcomponent
import treehou.se.habit.module.ActivityScope
import treehou.se.habit.module.FragmentComponent
import treehou.se.habit.module.FragmentComponentBuilder

@ActivityScope
@Subcomponent(modules = arrayOf(SitemapListModule::class))
interface SitemapListComponent : FragmentComponent<SitemapListFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<SitemapListModule, SitemapListComponent>
}