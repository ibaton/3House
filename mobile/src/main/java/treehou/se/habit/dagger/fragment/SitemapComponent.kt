package treehou.se.habit.dagger.fragment


import dagger.Subcomponent
import treehou.se.habit.dagger.FragmentComponent
import treehou.se.habit.dagger.FragmentComponentBuilder
import treehou.se.habit.dagger.scopes.ActivityScope
import treehou.se.habit.ui.sitemaps.sitemap.SitemapFragment

@ActivityScope
@Subcomponent(modules = arrayOf(SitemapModule::class))
interface SitemapComponent : FragmentComponent<SitemapFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<SitemapModule, SitemapComponent>
}