package treehou.se.habit.core.controller;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import treehou.se.habit.core.db.controller.CellDB;
import treehou.se.habit.core.db.controller.CellRowDB;

public class CellRow {

    private CellRowDB cellRowDB;

    public CellRow(CellRowDB cellRowDB) {
        this.cellRowDB = cellRowDB;
    }

    public CellRowDB getDB() {
        return cellRowDB;
    }

    public void setDB(CellRowDB cellRowDB) {
        this.cellRowDB = cellRowDB;
    }

    public long getId() {
        return getDB().getId();
    }

    public void setId(long id) {
        getDB().setId(id);
    }

    public List<Cell> getCells() {
        List<Cell> cells = new ArrayList<>();
        /*for (CellDB cell : getDB().getCells()) {
            cells.add(new Cell(cell));
        }*/

        return cells;
    }

    public void setCells(List<Cell> cells) {

        /*RealmList<CellDB> cellDbs = new RealmList<>();
        for (Cell cell : cells) {
            cellDbs.add(cell.getDB());
        }

        getDB().setCells(cellDbs);*/
    }

    public Controller getController() {
        return new Controller(getDB().getController());
    }

    public void setController(Controller controller) {
        setController(controller);
    }

    public static Cell addCell(CellRow cellRowDB){
        Cell cell = new Cell();
        cell.setCellRow(cellRowDB);
        //Cell.save(cell);

        return cell;
    }

    public static void save(CellRow item){
        //CellRowDB.save(item.getDB());
    }
}
