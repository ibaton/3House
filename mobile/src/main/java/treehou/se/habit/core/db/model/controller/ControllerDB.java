package treehou.se.habit.core.db.model.controller;

import android.graphics.Color;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ControllerDB extends RealmObject {

    @PrimaryKey
    private long id = 0;
    private String name;
    private int color = Color.parseColor("#33000000");
    private boolean showNotification = false;
    private boolean showTitle = true;
    private RealmList<CellRowDB> cellRows = new RealmList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isShowNotification() {
        return showNotification;
    }

    public void setShowNotification(boolean showNotification) {
        this.showNotification = showNotification;
    }

    public boolean isShowTitle() {
        return showTitle;
    }

    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    public RealmList<CellRowDB> getCellRows() {
        return cellRows;
    }

    public void setCellRows(RealmList<CellRowDB> cellRows) {
        this.cellRows = cellRows;
    }

    public static ControllerDB load(Realm realm, long id){
        return realm.where(ControllerDB.class).equalTo("id", id).findFirst();
    }

    public static void save(Realm realm, ControllerDB item){
        realm.beginTransaction();
        if(item.getId() <= 0) {
            item.setId(getUniqueId(realm));
        }
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
    }

    public CellRowDB addRow(Realm realm){
        CellRowDB cellRow = new CellRowDB();
        cellRow.setController(this);
        realm.beginTransaction();
        cellRows.add(cellRow);
        realm.commitTransaction();
        CellRowDB.save(realm,cellRow);

        CellDB cell = new CellDB();
        cell.setCellRow(cellRow);
        CellDB.save(realm, cell);

        return cellRow;
    }

    public static long getUniqueId(Realm realm) {
        Number num = realm.where(ControllerDB.class).max("id");
        long newId = 1;
        if (num != null) newId = num.longValue() + 1;
        return newId;
    }

    @Override
    public String toString() {
        return name;
    }
}
