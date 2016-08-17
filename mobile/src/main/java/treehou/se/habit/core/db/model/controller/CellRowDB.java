package treehou.se.habit.core.db.model.controller;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CellRowDB extends RealmObject {

    @PrimaryKey
    private long id = 0;
    private ControllerDB controller;
    private RealmList<CellDB> cells = new RealmList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RealmList<CellDB> getCells() {
        return cells;
    }

    public void setCells(RealmList<CellDB> cells) {
        this.cells = cells;
    }

    public ControllerDB getController() {
        return controller;
    }

    public void setController(ControllerDB controller) {
        this.controller = controller;
    }

    public CellDB addCell(Realm realm){
        CellDB cell = new CellDB();
        cell.setCellRow(this);
        CellDB cellDB = CellDB.save(realm, cell);

        realm.beginTransaction();
        cells.add(cellDB);
        realm.commitTransaction();

        return cell;
    }

    public static void save(Realm realm, CellRowDB item){
        realm.beginTransaction();
        if(item.getId() <= 0) {
            item.setId(getUniqueId(realm));
        }
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
    }

    public static long getUniqueId(Realm realm) {
        Number num = realm.where(CellRowDB.class).max("id");
        long newId = 1;
        if (num != null) newId = num.longValue() + 1;
        return newId;
    }
}
