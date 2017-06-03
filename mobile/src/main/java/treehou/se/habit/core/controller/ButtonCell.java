package treehou.se.habit.core.controller;

import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import treehou.se.habit.core.db.model.controller.ButtonCellDB;

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

    public OHItem getItem() {
        return null; //getDB().getItem();
    }

    public void setItem(OHItem item) {
        //getDB().setItem(item);
    }

    public String getIcon() {
        return getDB().getIcon();
    }

    public void setIcon(String icon) {
        getDB().setIcon(icon);
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
