package treehou.se.habit.main;

import android.os.Bundle;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.util.Settings;

public class MainPresenter implements MainContract.Presenter {

    private static final String TAG = MainPresenter.class.getSimpleName();
    private Realm realm;
    private MainContract.View mainView;
    private Settings settings;

    @Inject
    public MainPresenter(MainContract.View mainView, Realm realm, Settings settings) {
        this.mainView = mainView;
        this.realm = realm;
        this.settings = settings;
    }

    @Override
    public void load(Bundle savedData) {
        setupFragments(savedData);
    }


    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void unload() {
        realm.close();
    }

    /**
     * Setup the saved instance state.
     * @param savedInstanceState saved instance state
     */
    private void setupFragments(Bundle savedInstanceState){
        if(!mainView.hasOpenPage()) {

            // Load server setup server fragment if no server found
            RealmResults<ServerDB> serverDBs = realm.where(ServerDB.class).findAll();

            if(serverDBs.size() <= 0) {
                mainView.openServers();
            }else {
                // Load default sitemap if any
                String defaultSitemap = settings.getDefaultSitemap();
                if(savedInstanceState == null && defaultSitemap != null) {
                    mainView.openSitemaps(defaultSitemap);
                }else {
                    mainView.openSitemaps();
                }
            }
        }
    }

    @Override
    public void save(Bundle savedData) {}

    @Override
    public void showSitemaps() {
        mainView.openSitemaps();
    }

    @Override
    public void showSitemap(OHSitemap sitemap) {
        mainView.openSitemap(sitemap);
    }

    @Override
    public void showControllers() {
        mainView.openControllers();
    }

    @Override
    public void showServers() {
        mainView.openServers();
    }

    @Override
    public void showSettings() {
        mainView.openSettings();
    }
}
