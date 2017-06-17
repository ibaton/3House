package treehou.se.habit.core.db.model.controller;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

public class CellRowDB extends RealmObject {

    private ControllerDB controller;
    private RealmList<CellDB> cells = new RealmList<>();

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
}
