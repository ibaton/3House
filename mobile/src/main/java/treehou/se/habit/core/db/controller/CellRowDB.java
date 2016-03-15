package treehou.se.habit.core.db.controller;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import se.treehou.ng.ohcommunicator.core.db.OHRealm;

public class CellRowDB /*extends RealmObject*/ {

    //@PrimaryKey
    private long id = 0;
    private ControllerDB controller;
    //private RealmList<CellDB> cells;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
/*
    public RealmList<CellDB> getCells() {
        return cells;
    }

    public void setCells(RealmList<CellDB> cells) {
        this.cells = cells;
    }*/

    public ControllerDB getController() {
        return controller;
    }

    public void setController(ControllerDB controller) {
        this.controller = controller;
    }

    /*public static CellDB addCell(CellRowDB cellRowDB){
        CellDB cell = new CellDB();
        cell.setCellRow(cellRowDB);
        CellDB.save(cell);

        return cell;
    }

    public static void save(CellRowDB item){
        Realm realm = OHRealm.realm();
        realm.beginTransaction();
        if(item.getId() <= 0) {
            item.setId(getUniqueId());
        }
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
    }

    public static long getUniqueId() {
        Realm realm = OHRealm.realm();
        Number num = realm.where(CellRowDB.class).max("id");
        long newId = 1;
        if (num != null) newId = num.longValue() + 1;
        realm.close();
        return newId;
    }*/
}
