package treehou.se.habit.core.controller;

import java.util.ArrayList;
import java.util.List;

import treehou.se.habit.core.db.model.controller.ControllerDB;

public class Controller {

    private ControllerDB controllerDB;

    public Controller(ControllerDB controllerDB) {
        this.controllerDB = controllerDB;
    }

    public ControllerDB getDB() {
        return controllerDB;
    }

    public void setDB(ControllerDB controllerDB) {
        this.controllerDB = controllerDB;
    }

    public long getId() {
        return getDB().getId();
    }

    public void setId(long id) {
        getDB().setId(id);
    }

    public String getName() {
        return getDB().getName();
    }

    public void setName(String name) {
        getDB().setName(name);
    }

    public int getColor() {
        return getDB().getColor();
    }

    public void setColor(int color) {
        getDB().setColor(color);
    }

    public boolean isShowNotification() {
        return getDB().isShowNotification();
    }

    public void setShowNotification(boolean showNotification) {
        getDB().setShowNotification(showNotification);
    }

    public boolean isShowTitle() {
        return getDB().isShowTitle();
    }

    public void setShowTitle(boolean showTitle) {
        getDB().setShowTitle(showTitle);
    }

    public List<CellRow> getCellRows() {
        List<CellRow> rows = new ArrayList<>();
        /*for(CellRowDB cellRowDB : getDB().getCellRows()){
            rows.add(new CellRow(cellRowDB));
        }*/

        return rows;
    }

    public void setCellRows(List<CellRow> cellRows) {

        /*RealmList<CellRowDB> dbRows = new RealmList<>();
        for(CellRow cellRow : cellRows){
            dbRows.add(cellRow.getDB());
        }
        controllerDB.setCellRows(dbRows);*/
    }

    public static Controller load(long id){
        return null;//new Controller(ControllerDB.load(id));
    }

    public static void save(Controller item){
        //ControllerDB.saveServer(item.getDB());
    }

    public static CellRow addRow(Controller controller){
        return null;//new CellRow(ControllerDB.addRow(controller.getDB()));
    }
}
