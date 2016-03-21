package treehou.se.habit.core.controller;

import treehou.se.habit.core.db.model.controller.ChartCellDB;

public class ChartCell {

    private ChartCellDB cellDB;

    public ChartCell(ChartCellDB cellDB) {
        this.cellDB = cellDB;
    }

    public ChartCellDB getDB() {
        return cellDB;
    }

    public void setDB(ChartCellDB cellDB) {
        this.cellDB = cellDB;
    }

    public long getId() {
        return getDB().getId();
    }

    public void setId(long id) {
        getDB().setId(id);
    }

    public Cell getCell() {
        return new Cell(getDB().getCell());
    }

    public void setCell(Cell cell) {
        getDB().setCell(cell.getDB());
    }

    public void save(){
        //ChartCellDB.save(getDB());
    }
}
