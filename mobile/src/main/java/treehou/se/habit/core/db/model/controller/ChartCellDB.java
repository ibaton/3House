package treehou.se.habit.core.db.model.controller;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import treehou.se.habit.core.db.model.OHRealm;

public class ChartCellDB extends RealmObject {

    @PrimaryKey
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

    public static void save(Realm realm, ChartCellDB item){
        realm.beginTransaction();
        if(item.getId() <= 0) {
            item.setId(getUniqueId(realm));
        }
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
    }

    public static ChartCellDB getCell(Realm realm, CellDB cell){
        return realm.where(ChartCellDB.class).equalTo("cell.id", cell.getId()).findFirst();
    }

    public static long getUniqueId(Realm realm) {
        Number num = realm.where(ChartCellDB.class).max("id");
        long newId = 1;
        if (num != null) newId = num.longValue() + 1;
        return newId;
    }
}
