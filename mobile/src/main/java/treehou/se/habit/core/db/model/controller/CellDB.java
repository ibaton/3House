package treehou.se.habit.core.db.model.controller;

import android.graphics.Color;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CellDB extends RealmObject {

    public static final int TYPE_EMPTY         = 0;
    public static final int TYPE_BUTTON        = 1;
    public static final int TYPE_VOICE         = 2;
    public static final int TYPE_COLOR         = 3;
    public static final int TYPE_SLIDER        = 4;
    public static final int TYPE_INC_DEC       = 5;

    @PrimaryKey
    private long id = 0;
    private CellRowDB cellRow;
    private int type = TYPE_EMPTY;
    private int color = Color.parseColor("#33000000");
    private String label = "";

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public CellRowDB getCellRow() {
        return cellRow;
    }

    public void setCellRow(CellRowDB cellRow) {
        this.cellRow = cellRow;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public static CellDB load(Realm realm, long id){
        return realm.where(CellDB.class).equalTo("id", id).findFirst();
    }

    public static CellDB save(Realm realm, CellDB item){
        realm.beginTransaction();
        if(item.getId() <= 0) {
            item.setId(getUniqueId(realm));
        }
        CellDB cell = realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();

        return cell;
    }

    public static long getUniqueId(Realm realm) {
        Number num = realm.where(CellDB.class).max("id");
        long newId = 1;
        if (num != null) newId = num.longValue() + 1;
        return newId;
    }
}
