package treehou.se.habit.dagger.fragment


import android.os.Bundle
import dagger.Module
import dagger.Provides
import io.realm.Realm
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.util.GsonHelper
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.dagger.ViewModule
import treehou.se.habit.ui.sitemaps.page.PageContract
import treehou.se.habit.ui.sitemaps.page.PageFragment
import treehou.se.habit.ui.sitemaps.page.PagePresenter
import javax.inject.Named

@Module
class PageModule(fragment: PageFragment, private val args: Bundle) : ViewModule<PageFragment>(fragment) {

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

    @Provides
    fun provideServer(@Named("arguments") args: Bundle, realm: Realm): OHServer {
        val gson = GsonHelper.createGsonBuilder()

        val serverId = args.getLong(PageContract.ARG_SERVER)

        return ServerDB.load(realm, serverId)?.toGeneric() ?: OHServer()
    }

    @Provides
    fun providePage(@Named("arguments") args: Bundle): OHLinkedPage {
        val gson = GsonHelper.createGsonBuilder()

        var page = args.getString(PageContract.ARG_PAGE)
        return gson.fromJson(page, OHLinkedPage::class.java)
    }
}
