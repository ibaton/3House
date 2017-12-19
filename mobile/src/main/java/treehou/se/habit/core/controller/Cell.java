package treehou.se.habit.core.controller;

import treehou.se.habit.core.db.model.controller.CellDB;

public class Cell {

    public static final int TYPE_EMPTY         = 0;
    public static final int TYPE_BUTTON        = 1;
    public static final int TYPE_VOICE         = 2;
    public static final int TYPE_COLOR         = 3;
    public static final int TYPE_SLIDER        = 4;
    public static final int TYPE_INC_DEC       = 5;

    private CellDB cellDB;

    public Cell() {
        cellDB = new CellDB();
    }

    public Cell(CellDB cellDB) {
        this.cellDB = cellDB;
    }

    public CellDB getDB() {
        return cellDB;
    }

    public void setDB(CellDB cellDB) {
        this.cellDB = cellDB;
    }

    public int getType() {
        return getDB().getType();
    }

    public CellRow getCellRow() {
        return new CellRow(getDB().getCellRow());
    }

    public void setCellRow(CellRow cellRow) {
        getDB().setCellRow(cellRow.getDB());
    }

    public String getLabel() {
        return cellDB.getLabel();
    }

    public void setLabel(String label) {
        cellDB.setLabel(label);
    }

    public int getColor() {
        return cellDB.getColor();
    }

    public void setColor(int color) {
        cellDB.setColor(color);
    }

    public static Cell load(int id){
        return null;// new Cell(CellDB.load(id));
    }

    /*public static void saveServer(Cell item){
        CellDB.saveServer(item.getDB());
    }*/
}
