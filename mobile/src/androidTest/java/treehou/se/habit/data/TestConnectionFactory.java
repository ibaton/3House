package treehou.se.habit.data;

import android.content.Context;

import java.util.List;

import rx.Observable;
import se.treehou.ng.ohcommunicator.connector.models.OHBinding;
import se.treehou.ng.ohcommunicator.connector.models.OHInboxItem;
import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback;
import treehou.se.habit.util.ConnectionFactory;

public class TestConnectionFactory extends ConnectionFactory {

    public IServerHandler createServerHandler(OHServer server, Context context){
        return new Connector.ServerHandler(server, context);
    }

    public static class TestServerHandler implements IServerHandler{

        @Override
        public void requestBindings(OHCallback<List<OHBinding>> ohCallback) {

        }

        @Override
        public Observable<List<OHBinding>> requestBindingsRx() {
            return null;
        }

        @Override
        public void requestInboxItems(OHCallback<List<OHInboxItem>> ohCallback) {

        }

        @Override
        public Observable<List<OHInboxItem>> requestInboxItemsRx() {
            return null;
        }

        @Override
        public void requestItem(String s, OHCallback<OHItem> ohCallback) {

        }

        @Override
        public Connector.ServerHandler.PageRequestTask requestPageUpdates(OHServer ohServer, OHLinkedPage ohLinkedPage, OHCallback<OHLinkedPage> ohCallback) {
            return null;
        }

        @Override
        public Observable<OHLinkedPage> requestPageUpdatesRx(OHServer ohServer, OHLinkedPage ohLinkedPage) {
            return null;
        }

        @Override
        public void requestItems(OHCallback<List<OHItem>> ohCallback) {

        }

        @Override
        public Observable<List<OHItem>> requestItemsRx() {
            return null;
        }

        @Override
        public void requestPage(OHLinkedPage ohLinkedPage, OHCallback<OHLinkedPage> ohCallback) {

        }

        @Override
        public Observable<OHItem> requestItemRx(String s) {
            return null;
        }

        @Override
        public Observable<OHLinkedPage> requestPageRx(OHLinkedPage ohLinkedPage) {
            return null;
        }

        @Override
        public String getUrl() {
            return null;
        }

        @Override
        public void approveInboxItem(OHInboxItem ohInboxItem) {

        }

        @Override
        public void ignoreInboxItem(OHInboxItem ohInboxItem) {

        }

        @Override
        public void unignoreInboxItem(OHInboxItem ohInboxItem) {

        }

        @Override
        public void sendCommand(String s, String s1) {

        }

        @Override
        public void requestSitemaps(OHCallback<List<OHSitemap>> ohCallback) {

        }

        @Override
        public Observable<List<OHSitemap>> requestSitemapRx() {
            return null;
        }
    }
}
