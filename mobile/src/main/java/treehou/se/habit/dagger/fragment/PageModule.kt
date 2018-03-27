package treehou.se.habit.dagger.fragment


import android.os.Bundle
import dagger.Module
import dagger.Provides
import treehou.se.habit.dagger.ViewModule
import treehou.se.habit.ui.sitemaps.page.PageContract
import treehou.se.habit.ui.sitemaps.page.PageFragment
import treehou.se.habit.ui.sitemaps.page.PagePresenter
import javax.inject.Named

@Module
class PageModule(fragment: PageFragment, protected val args: Bundle) : ViewModule<PageFragment>(fragment) {

    @Provides
    fun provideView(): PageContract.View {
        return view
    }

    @Provides
    fun providePresenter(presenter: PagePresenter): PageContract.Presenter {
        return presenter
    }

    @Provides
    @Named("arguments")
    fun provideArgs(): Bundle {
        return args
    }
}
