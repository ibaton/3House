package treehou.se.habit.dagger.fragment

import dagger.Subcomponent
import treehou.se.habit.dagger.FragmentComponent
import treehou.se.habit.dagger.FragmentComponentBuilder
import treehou.se.habit.dagger.scopes.ActivityScope
import treehou.se.habit.ui.sitemaps.sitemaplist.SitemapListFragment

@ActivityScope
@Subcomponent(modules = arrayOf(SitemapListModule::class))
interface SitemapListComponent : FragmentComponent<SitemapListFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<SitemapListModule, SitemapListComponent>
}