package treehou.se.habit.core.db.controller;

import io.realm.RealmObject;

public class TestDB extends RealmObject {

    private long id = 0;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
