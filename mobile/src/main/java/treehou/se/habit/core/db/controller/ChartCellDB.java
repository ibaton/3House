package treehou.se.habit.core.db.controller;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import se.treehou.ng.ohcommunicator.core.db.OHRealm;
import treehou.se.habit.core.controller.ChartCell;

public class ChartCellDB /*extends RealmObject*/ {

    //@PrimaryKey
    private long id = 0;
    private CellDB cell;

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

    /*public static void save(ChartCellDB item){
        Realm realm = OHRealm.realm();
        realm.beginTransaction();
        if(item.getId() <= 0) {
            item.setId(getUniqueId());
        }
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
    }

    public static ChartCellDB getCell(CellDB cell){
        return OHRealm.realm().where(ChartCellDB.class).equalTo("cell.id", cell.getId()).findFirst();
    }

    public static long getUniqueId() {
        Realm realm = OHRealm.realm();
        Number num = realm.where(ChartCellDB.class).max("id");
        long newId = 1;
        if (num != null) newId = num.longValue() + 1;
        realm.close();
        return newId;
    }*/
}
