package treehou.se.habit.module;

import android.content.Context;
import android.support.v4.util.Pair;

import java.util.List;

import io.realm.Realm;
import rx.Observable;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import treehou.se.habit.core.db.model.ServerDB;

public interface ServerLoaderFactory {

    OHServer loadServer(Realm realm, long id);
    Observable.Transformer<Realm, OHServer> loadServersRx();
    Observable.Transformer<OHServer, Pair<OHServer, List<OHSitemap>>> serverToSitemap(Context context);
    Observable.Transformer<Pair<OHServer,List<OHSitemap>>, Pair<OHServer,List<OHSitemap>>> filterDisplaySitemaps();
}
