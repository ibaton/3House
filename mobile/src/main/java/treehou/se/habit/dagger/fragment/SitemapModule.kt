package treehou.se.habit.dagger.fragment


import android.os.Bundle
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import io.realm.Realm
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.dagger.ViewModule
import treehou.se.habit.ui.sitemaps.sitemap.SitemapContract
import treehou.se.habit.ui.sitemaps.sitemap.SitemapContract.Presenter
import treehou.se.habit.ui.sitemaps.sitemap.SitemapFragment
import treehou.se.habit.ui.sitemaps.sitemap.SitemapPresenter
import javax.inject.Named

@Module
class SitemapModule(fragment: SitemapFragment, protected val args: Bundle) : ViewModule<SitemapFragment>(fragment) {

    @Provides
    fun providePresenter(presenter: SitemapPresenter): Presenter {
        return presenter
    }

    @Provides
    fun provideView(): SitemapContract.View {
        return view
    }

    @Provides
    @Named("arguments")
    fun provideArgs(): Bundle {
        return args
    }

    @Provides
    fun provideServer(realm: Realm, @Named("arguments") args: Bundle): ServerDB? {
        val serverId = args.getLong(Presenter.ARG_SERVER)
        return ServerDB.load(realm, serverId)
    }

    @Provides
    fun provideSitemap(gson: Gson, @Named("arguments") args: Bundle): OHSitemap? {
        val jSitemap = args.getString(Presenter.ARG_SITEMAP)
        return gson.fromJson(jSitemap, OHSitemap::class.java)
    }
}
