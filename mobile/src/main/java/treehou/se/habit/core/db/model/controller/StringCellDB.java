package treehou.se.habit.core.db.model.controller;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class StringCellDB extends RealmObject {

    @PrimaryKey
    private long id = 0;
    private CellDB cell;
    private String command;
    private String icon;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public static void save(Realm realm, StringCellDB item){
        realm.beginTransaction();
        if(item.getId() <= 0) {
            item.setId(getUniqueId(realm));
        }
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
    }

    public static StringCellDB getCell(Realm realm, CellDB cell){
        return realm.where(StringCellDB.class).equalTo("cell.id", cell.getId()).findFirst();
    }

    public static long getUniqueId(Realm realm) {
        Number num = realm.where(StringCellDB.class).max("id");
        long newId = 1;
        if (num != null) newId = num.longValue() + 1;
        return newId;
    }
}
