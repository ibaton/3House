package treehou.se.habit.core.controller;

import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import treehou.se.habit.core.db.model.controller.SliderCellDB;

public class SliderCell {

    public static final int TYPE_MAX = 0;
    public static final int TYPE_MIN = 1;
    public static final int TYPE_SLIDER = 2;
    public static final int TYPE_CHART = 3;

    private SliderCellDB sliderCellDB;

    public SliderCell() {
    }

    public SliderCell(SliderCellDB sliderCellDB) {
        this.sliderCellDB = sliderCellDB;
    }

    public SliderCellDB getDB() {
        return sliderCellDB;
    }

    public void setDB(SliderCellDB sliderCellDB) {
        this.sliderCellDB = sliderCellDB;
    }

    public OHItem getItem() {
        return null; //new OHItem(sliderCellDB.getItem());
    }

    public void setItem(OHItem item) {
        //sliderCellDB.setItem(item.getDB());
    }

    public String getIcon() {
        return sliderCellDB.getIcon();
    }

    public void setIcon(String icon) {
        sliderCellDB.setIcon(icon);
    }

    public int getType() {
        return sliderCellDB.getType();
    }

    public void setType(int type) {
        sliderCellDB.setType(type);
    }

    public void setMin(int min) {
        sliderCellDB.setMin(min);
    }

    public int getMin() {
        return sliderCellDB.getMin();
    }

    public void setMax(int max) {
        sliderCellDB.setMax(max);
    }

    public int getMax() {
        return sliderCellDB.getMax();
    }

    public static void save(SliderCell item){
        //SliderCellDB.saveServer(item.getDB());
    }

    public static SliderCell getCell(Cell cell){
        return SliderCell.getCell(cell);
    }
}
