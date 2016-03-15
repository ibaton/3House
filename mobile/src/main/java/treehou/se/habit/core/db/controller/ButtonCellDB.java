package treehou.se.habit.core.db.controller;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import se.treehou.ng.ohcommunicator.core.db.OHItemDB;
import se.treehou.ng.ohcommunicator.core.db.OHRealm;

public class ButtonCellDB {

    private long id = 0;
    private String icon;
    private String command;
    private CellDB cell;
    private OHItemDB item;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public OHItemDB getItem() {
        return item;
    }

    public void setItem(OHItemDB item) {
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

    /*public static void save(ButtonCellDB item){
        Realm realm = OHRealm.realm();
        realm.beginTransaction();
        if(item.getId() <= 0) {
            item.setId(getUniqueId());
        }
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
    }

    public static ButtonCellDB getCell(CellDB cell){
        Realm realm = OHRealm.realm();
        ButtonCellDB cellDB = realm.where(ButtonCellDB.class).equalTo("cell.id", cell.getId()).findFirst();
        realm.close();
        return cellDB;
    }

    public static long getUniqueId() {
        Realm realm = OHRealm.realm();
        Number num = realm.where(ButtonCellDB.class).max("id");
        long newId = 1;
        if (num != null) newId = num.longValue() + 1;
        realm.close();
        return newId;
    }*/
}
