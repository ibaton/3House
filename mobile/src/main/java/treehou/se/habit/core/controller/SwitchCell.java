package treehou.se.habit.core.controller;

import treehou.se.habit.core.db.model.controller.SwitchCellDB;

public class SwitchCell {

    private SwitchCellDB switchCellDB;

    public SwitchCell() {
    }

    public SwitchCell(SwitchCellDB switchCellDB) {
        this.switchCellDB = switchCellDB;
    }

    public SwitchCellDB getDB() {
        return switchCellDB;
    }

    public void setDB(SwitchCellDB switchCellDB) {
        this.switchCellDB = switchCellDB;
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

    public static void save(SwitchCell item){
        //SwitchCellDB.save(item.getDB());
    }

    public static SwitchCell getCell(Cell cell){
        return null;//new SwitchCell(SwitchCellDB.getCell(cell.getDB()));
    }
}
