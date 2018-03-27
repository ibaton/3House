package treehou.se.habit.dagger.fragment


import dagger.Subcomponent
import treehou.se.habit.dagger.FragmentComponent
import treehou.se.habit.dagger.FragmentComponentBuilder
import treehou.se.habit.dagger.scopes.ActivityScope
import treehou.se.habit.ui.servers.sitemaps.sitemapsettings.SitemapSettingsFragment

@ActivityScope
@Subcomponent(modules = arrayOf(SitemapSettingsModule::class))
interface SitemapSettingsComponent : FragmentComponent<SitemapSettingsFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<SitemapSettingsModule, SitemapSettingsComponent>
}