package treehou.se.habit.core.db.model.controller;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import treehou.se.habit.core.db.model.ItemDB;

public class SliderCellDB extends RealmObject {

    public static final int TYPE_MAX = 0;
    public static final int TYPE_MIN = 1;
    public static final int TYPE_SLIDER = 2;
    public static final int TYPE_CHART = 3;

    private String icon;
    private int type;
    private ItemDB item;
    private int min = 0;
    private int max = 100;

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
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
    }
}
