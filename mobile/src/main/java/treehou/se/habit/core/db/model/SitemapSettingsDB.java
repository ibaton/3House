package treehou.se.habit.core.db.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SitemapSettingsDB extends RealmObject {

    @PrimaryKey
    private long id;
    private boolean display = true;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }
}
