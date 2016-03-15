package treehou.se.habit.core.db.model;

public class OHLinkedPage /*extends RealmObject*/ {

    private String id;
    private String link;
    private String title;
    private boolean leaf;
    //private RealmList<OHWidget> widget;

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

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    /*public RealmList<OHWidget> getWidget() {
        //return widget;
        return null;
    }

    public void setWidget(RealmList<OHWidget> widget) {
        //this.widget = widget;
    }*/
}
