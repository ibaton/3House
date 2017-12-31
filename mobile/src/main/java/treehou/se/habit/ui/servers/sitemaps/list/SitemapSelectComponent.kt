package treehou.se.habit.ui.servers.sitemaps.list


import dagger.Subcomponent
import treehou.se.habit.module.ActivityScope
import treehou.se.habit.module.FragmentComponent
import treehou.se.habit.module.FragmentComponentBuilder

@ActivityScope
@Subcomponent(modules = arrayOf(SitemapSelectModule::class))
interface SitemapSelectComponent : FragmentComponent<SitemapSelectFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<SitemapSelectModule, SitemapSelectComponent>
}