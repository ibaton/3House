package treehou.se.habit.core.controller;

import se.treehou.ng.ohcommunicator.core.db.OHItemDB;
import treehou.se.habit.core.db.controller.ButtonCellDB;

public class ButtonCell {

    private ButtonCellDB buttonCellDB;

    public ButtonCell(ButtonCellDB buttonCellDB) {
        this.buttonCellDB = buttonCellDB;
    }

    public ButtonCellDB getDB() {
        return buttonCellDB;
    }

    public void setDB(ButtonCellDB cellDB) {
        this.buttonCellDB = cellDB;
    }

    public long getId() {
        return getDB().getId();
    }

    public void setId(long id) {
        getDB().setId(id);
    }

    public OHItemDB getItem() {
        return getDB().getItem();
    }

    public void setItem(OHItemDB item) {
        getDB().setItem(item);
    }

    public String getIcon() {
        return getDB().getIcon();
    }

    public void setIcon(String icon) {
        getDB().setIcon(icon);
    }

    public Cell getCell() {
        return new Cell(getDB().getCell());
    }

    public void setCell(Cell cell) {
        getDB().setCell(cell.getDB());
    }

    public String getCommand() {
        return buttonCellDB.getCommand();
    }

    public void setCommand(String command) {
        buttonCellDB.setCommand(command);
    }

    /*public void save(ButtonCell item){
        ButtonCellDB.save(item.getDB());
    }*/
}
