package treehou.se.habit.core.db.controller;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import se.treehou.ng.ohcommunicator.core.db.OHRealm;

public class SwitchCellDB /*extends RealmObject*/ {

    //@PrimaryKey
    private long id = 0;
    private String icon;
    private CellDB cell;
    private String command;

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

    /*public static void save(SwitchCellDB item){
        Realm realm = OHRealm.realm();
        realm.beginTransaction();
        if(item.getId() <= 0) {
            item.setId(getUniqueId());
        }
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
    }

    public static SwitchCellDB getCell(CellDB cell){
        return OHRealm.realm().where(SwitchCellDB.class).equalTo("cell.id", cell.getId()).findFirst();
    }

    public static long getUniqueId() {
        Realm realm = OHRealm.realm();
        Number num = realm.where(SwitchCellDB.class).max("id");
        long newId = 1;
        if (num != null) newId = num.longValue() + 1;
        realm.close();
        return newId;
    }*/
}
