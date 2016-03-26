package treehou.se.habit.core.db.model.controller;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import treehou.se.habit.core.db.model.ItemDB;
import treehou.se.habit.core.db.model.OHRealm;

public class SliderCellDB extends RealmObject {

    public static final int TYPE_MAX = 0;
    public static final int TYPE_MIN = 1;
    public static final int TYPE_SLIDER = 2;
    public static final int TYPE_CHART = 3;

    @PrimaryKey
    private long id = 0;
    private String icon;
    private CellDB cell;
    private int type;
    private ItemDB item;
    private int min = 0;
    private int max = 100;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ItemDB getItem() {
        return item;
    }

    public void setItem(ItemDB item) {
        this.item = item;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public CellDB getCell() {
        return cell;
    }

    public void setCell(CellDB cell) {
        this.cell = cell;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMin() {
        return min;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMax() {
        return max;
    }

    public static void save(Realm realm, SliderCellDB item){
        realm.beginTransaction();
        if(item.getId() <= 0) {
            item.setId(getUniqueId(realm));
        }
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
    }

    public static SliderCellDB getCell(Realm realm, CellDB cell){
        return realm.where(SliderCellDB.class).equalTo("cell.id", cell.getId()).findFirst();
    }

    public static long getUniqueId(Realm realm) {
        Number num = realm.where(SliderCellDB.class).max("id");
        long newId = 1;
        if (num != null) newId = num.longValue() + 1;
        return newId;
    }
}
