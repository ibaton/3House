package treehou.se.habit.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LinkedPage {

    private String id;
    private String link;
    private String title;
    private String leaf;
    private List<Widget> widget;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getBaseUrl(){
        try {
            URL url = new URL(link);
            return new URL(url.getProtocol(), url.getHost(), url.getPort(), "", null).toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getLeaf() {
        return leaf;
    }

    public void setLeaf(String leaf) {
        this.leaf = leaf;
    }

    public List<Widget> getWidget() {
        return widget!=null?widget:new ArrayList<Widget>();
    }

    public void setWidget(List<Widget> widget) {
        this.widget = widget;
    }
}
