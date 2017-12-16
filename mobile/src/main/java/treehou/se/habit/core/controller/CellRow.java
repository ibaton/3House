package treehou.se.habit.core.controller;

import java.util.ArrayList;
import java.util.List;

import treehou.se.habit.core.db.model.controller.CellRowDB;

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

    public Controller getController() {
        return new Controller(getDB().getController());
    }

    public void setController(Controller controller) {
        setController(controller);
    }

    public static Cell addCell(CellRow cellRowDB){
        Cell cell = new Cell();
        cell.setCellRow(cellRowDB);
        //Cell.saveServer(cell);

        return cell;
    }

    public static void save(CellRow item){
        //CellRowDB.saveServer(item.getDB());
    }
}
