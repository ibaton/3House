package treehou.se.habit.ui.sitemaps.sitemap;


import android.os.Bundle;

import com.google.gson.Gson;

import javax.inject.Named;
import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.module.ViewModule;
import treehou.se.habit.ui.sitemaps.sitemap.SitemapContract.Presenter;

@Module
public class SitemapModule extends ViewModule<SitemapFragment> {

    protected final Bundle args;

    public SitemapModule(SitemapFragment fragment, Bundle args) {
        super(fragment);
        this.args = args;
    }

    @Provides
    public Presenter providePresenter(SitemapPresenter presenter) {
        return presenter;
    }

    @Provides
    public SitemapContract.View provideView() {
        return view;
    }

    @Provides
    @Named("arguments")
    public Bundle provideArgs() {
        return args;
    }

    @Provides
    public ServerDB provideServer(Realm realm, @Named("arguments") Bundle args) {
        long serverId = args.getLong(Presenter.ARG_SERVER);
        return ServerDB.load(realm, serverId);
    }

    @Provides
    public OHSitemap provideSitemap(Gson gson, @Named("arguments") Bundle args) {
        String jSitemap = args.getString(Presenter.ARG_SITEMAP);
        return gson.fromJson(jSitemap, OHSitemap.class);
    }
}
