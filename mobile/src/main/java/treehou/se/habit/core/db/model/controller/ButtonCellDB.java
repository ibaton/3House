package treehou.se.habit.core.db.model.controller;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import treehou.se.habit.core.db.model.ItemDB;

public class ButtonCellDB extends RealmObject {

    @PrimaryKey
    private long id = 0;
    private String icon;
    private String command;
    private CellDB cell;
    private ItemDB item;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public CellDB getCell() {
        return cell;
    }

    public void setCell(CellDB cell) {
        this.cell = cell;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public static ButtonCellDB save(Realm realm, ButtonCellDB item){
        realm.beginTransaction();
        if(item.getId() <= 0) {
            item.setId(getUniqueId(realm));
        }
        ButtonCellDB buttonCellDB = realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
        return buttonCellDB;
    }

    public static ButtonCellDB getCell(Realm realm, CellDB cell){
        return realm.where(ButtonCellDB.class).equalTo("cell.id", cell.getId()).findFirst();
    }

    public static long getUniqueId(Realm realm) {
        Number num = realm.where(ButtonCellDB.class).max("id");
        long newId = 1;
        if (num != null) newId = num.longValue() + 1;
        return newId;
    }
}
