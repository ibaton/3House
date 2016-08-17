package treehou.se.habit.core.controller;

import treehou.se.habit.core.db.model.controller.StringCellDB;

public class StringCell {

    private StringCellDB stringCellDB;

    public StringCell() {
    }

    public StringCell(StringCellDB stringCellDB) {
        this.stringCellDB = stringCellDB;
    }

    public StringCellDB getDB() {
        return stringCellDB;
    }

    public void setDB(StringCellDB stringCellDB) {
        this.stringCellDB = stringCellDB;
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

    public String getCommand() {
        return getDB().getCommand();
    }

    public void setCommand(String command) {
        getDB().setCommand(command);
    }

    public String getIcon() {
        return getDB().getIcon();
    }

    public void setIcon(String icon) {
        getDB().setIcon(icon);
    }

    public static void save(StringCell item){
        //StringCellDB.save(item.getDB());
    }

    public static StringCell getCell(Cell cell){
        return null;//new StringCell(StringCellDB.getCell(cell.getDB()));
    }
}
