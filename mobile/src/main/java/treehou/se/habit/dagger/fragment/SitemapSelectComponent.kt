package treehou.se.habit.dagger.fragment


import dagger.Subcomponent
import treehou.se.habit.dagger.FragmentComponent
import treehou.se.habit.dagger.FragmentComponentBuilder
import treehou.se.habit.dagger.scopes.ActivityScope
import treehou.se.habit.ui.servers.sitemaps.list.SitemapSelectFragment

@ActivityScope
@Subcomponent(modules = arrayOf(SitemapSelectModule::class))
interface SitemapSelectComponent : FragmentComponent<SitemapSelectFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<SitemapSelectModule, SitemapSelectComponent>
}