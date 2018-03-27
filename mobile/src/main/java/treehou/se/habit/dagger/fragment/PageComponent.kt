package treehou.se.habit.dagger.fragment


import dagger.Subcomponent
import treehou.se.habit.dagger.FragmentComponent
import treehou.se.habit.dagger.FragmentComponentBuilder
import treehou.se.habit.dagger.scopes.ActivityScope
import treehou.se.habit.ui.sitemaps.page.PageFragment

@ActivityScope
@Subcomponent(modules = arrayOf(PageModule::class))
interface PageComponent : FragmentComponent<PageFragment> {

    @Subcomponent.Builder
    interface Builder : FragmentComponentBuilder<PageModule, PageComponent>
}