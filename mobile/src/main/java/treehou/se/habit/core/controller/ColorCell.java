package treehou.se.habit.core.controller;

import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import treehou.se.habit.core.db.model.controller.ColorCellDB;

public class ColorCell {

    private ColorCellDB colorCellDB;

    public ColorCell() {
    }

    public ColorCell(ColorCellDB colorCellDB) {
        this.colorCellDB = colorCellDB;
    }

    public ColorCellDB getDB() {
        return colorCellDB;
    }

    public void setDB(ColorCellDB colorCellDB) {
        this.colorCellDB = colorCellDB;
    }

    public OHItem getItem() {
        return null; /*getDB().getItem();*/
    }

    public void setItem(OHItem item) {
        //getDB().setItem(item.getDB());
    }

    public String getIcon() {
        return getDB().getIcon();
    }

    public void setIcon(String iconOn) {
        getDB().setIcon(iconOn);
    }

    public String getCommand() {
        return getDB().getCommand();
    }

    public void setCommand(String command) {
        getDB().setCommand(command);
    }

    public static void save(ColorCell item){
        //ColorCellDB.save(item.getDB());
    }

    public static ColorCell getCell(Cell cell){
        return null;//new ColorCell(ColorCellDB.getCell(cell.getDB()));
    }
}
