package treehou.se.habit.ui.sitemaps.sitemap


import dagger.Subcomponent
import treehou.se.habit.module.ActivityScope
import treehou.se.habit.module.FragmentComponent
import treehou.se.habit.module.FragmentComponentBuilder

@ActivityScope
@Subcomponent(modules = arrayOf(SitemapModule::class))
interface SitemapComponent : FragmentComponent<SitemapFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<SitemapModule, SitemapComponent>
}