package se.treehou.ng.ohcommunicator.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import se.treehou.ng.ohcommunicator.core.db.OHLinkedPage;
import se.treehou.ng.ohcommunicator.core.db.OHWidget;

public class OHLinkedPageWrapper {

    private OHLinkedPage linkedPageDb;

    public OHLinkedPageWrapper() {
    }

    public OHLinkedPageWrapper(OHLinkedPage linkedPageDb) {
        this.linkedPageDb = linkedPageDb;
    }

    public OHLinkedPage getDb() {
        return linkedPageDb;
    }

    public void setDb(OHLinkedPage linkedPageDb) {
        this.linkedPageDb = linkedPageDb;
    }

    public String getId() {
        return getDb().getId();
    }

    public void setId(String id) {
        getDb().setId(id);
    }

    public String getLink() {
        return getDb().getLink();
    }

    public void setLink(String link) {
        getDb().setLink(link);
    }

    public String getTitle() {
        return getDb().getTitle();
    }

    public String getActionbarTitle(){
        return getTitle().replaceAll("(\\[)(.*)(\\])", "$2");
    }

    public void setTitle(String title) {
        getDb().setTitle(title);
    }

    public String getBaseUrl(){
        try {
            URL url = new URL(getTitle());
            return new URL(url.getProtocol(), url.getHost(), url.getPort(), "", null).toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getPath(){
        try {
            URL url = new URL(getLink());
            if(url.getPath().length() > 0) {
                return url.getPath().substring(1);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean getLeaf() {
        return getDb().isLeaf();
    }

    public void setLeaf(boolean leaf) {
        getDb().setLeaf(leaf);
    }

    public List<OHWidgetWrapper> getWidget() {
        List<OHWidgetWrapper> widgets = new ArrayList<>();

        /*for(OHWidget widgetDb : getDb().getWidget()){
            widgets.add(new OHWidgetWrapper(widgetDb));
        }*/

        return widgets;
    }

    public void setWidgets(List<OHWidgetWrapper> widgets) {
        /*RealmList<OHWidget> widgetDBs = new RealmList<>();
        for(OHWidgetWrapper widget : widgets){
            widgetDBs.add(widget.getDB());
        }

        getDb().setWidget(widgetDBs);*/
    }
}
