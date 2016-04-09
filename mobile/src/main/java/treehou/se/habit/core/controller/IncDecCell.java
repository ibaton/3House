package treehou.se.habit.core.controller;

import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import treehou.se.habit.core.db.model.controller.IncDecCellDB;

public class IncDecCell {

    private IncDecCellDB incDecCellDB;

    public IncDecCell() {
    }

    public IncDecCell(IncDecCellDB incDecCellDB) {
        this.incDecCellDB = incDecCellDB;
    }

    public IncDecCellDB getDB() {
        return incDecCellDB;
    }

    public void setDB(IncDecCellDB controllerDB) {
        this.incDecCellDB = controllerDB;
    }

    public long getId() {
        return incDecCellDB.getId();
    }

    public void setId(long id) {
        incDecCellDB.setId(id);
    }

    public OHItem getItem() {
        return null; // new OHItem(incDecCellDB.getItem());
    }

    public void setItem(OHItem item) {
        //getDB().setItem(item.getDB());
    }

    public String getIcon() {
        return incDecCellDB.getIcon();
    }

    public void setIcon(String icon) {
        getDB().setIcon(icon);
    }

    public Cell getCell() {
        return new Cell(incDecCellDB.getCell());
    }

    public void setCell(Cell cell) {
        getDB().setCell(cell.getDB());
    }

    public int getType() {
        return incDecCellDB.getType();
    }

    public void setType(int type) {
        getDB().setType(type);
    }

    public void setMin(int min) {
        getDB().setMin(min);
    }

    public int getValue() {
        return getDB().getValue();
    }

    public void setValue(int value) {
        getDB().setValue(value);
    }

    public int getMin() {
        return getDB().getMin();
    }

    public void setMax(int max) {
        getDB().setMax(max);
    }

    public int getMax() {
        return getDB().getMax();
    }

    public void save(){
        //IncDecCellDB.save(getDB());
    }

    public static IncDecCell getCell(Cell cell){
        return null;//new IncDecCell(IncDecCellDB.getCell(cell.getDB()));
    }
}
