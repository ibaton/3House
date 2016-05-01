package se.treehou.ng.ohcommunicator.connector.models;

import java.util.ArrayList;
import java.util.List;

import se.treehou.ng.ohcommunicator.connector.ConnectorUtil;

public class OHLinkedPage {

    private String id = "";
    private String link;
    private String title;
    private boolean leaf;
    private List<OHWidget> widget = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if(id == null) id = "";
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public List<OHWidget> getWidgets() {
        return widget;
    }

    public void setWidgets(List<OHWidget> widget) {
        this.widget = widget;
    }

    public String getBaseUrl(){
        return ConnectorUtil.getBaseUrl(getLink());
    }
}
