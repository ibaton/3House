package treehou.se.habit.module;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import rx.Observable;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;

public interface ServerLoaderFactory {

    OHServer loadServer(Realm realm, long id);
    Observable.Transformer<Realm, OHServer> loadServersRx();
    Observable.Transformer<OHServer, ServerSitemapsResponse> serverToSitemap(Context context);
    Observable.Transformer<List<OHServer>, List<ServerSitemapsResponse>> serversToSitemap(Context context);
    Observable.Transformer<ServerSitemapsResponse, ServerSitemapsResponse> filterDisplaySitemaps();
    Observable.Transformer<List<ServerSitemapsResponse>, List<ServerSitemapsResponse>> filterDisplaySitemapsList();
    Observable.Transformer<Realm, List<OHServer>> loadAllServersRx();

    ServerSitemapsResponse EMPTY_RESPONSE = new ServerSitemapsResponse(null, new ArrayList<>());

    class ServerSitemapsResponse{
        private OHServer server;
        private List<OHSitemap> sitemaps;
        private Throwable error;

        public ServerSitemapsResponse(OHServer server, List<OHSitemap> sitemaps) {
            this.server = server;
            this.sitemaps = sitemaps;
        }

        public ServerSitemapsResponse(OHServer server, List<OHSitemap> sitemaps, Throwable error) {
            this.server = server;
            this.sitemaps = sitemaps;
            this.error = error;
        }

        public boolean hasError(){
            return error!=null;
        }

        public OHServer getServer() {
            return server;
        }

        public List<OHSitemap> getSitemaps() {
            return sitemaps;
        }

        public Throwable getError() {
            return error;
        }
    }
}
