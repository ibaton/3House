package treehou.se.habit.core.db.model.controller;

import io.realm.RealmObject;
import treehou.se.habit.core.db.model.ItemDB;

public class ButtonCellDB extends RealmObject {

    private String icon;
    private String command;
    private ItemDB item;

    public ItemDB getItem() {
        return item;
    }

    public void setItem(ItemDB item) {
        this.item = item;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
