package treehou.se.habit.core.db.controller;

import android.content.Context;
import android.graphics.Color;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

import treehou.se.habit.ui.control.ControlHelper;

@Table(name = "Controllers")
public class ControllerDB extends Model {

    @Column(name = "Name")
    private String name;

    @Column(name = "color")
    private int color = Color.parseColor("#33000000");

    @Column(name = "showNotification")
    private boolean showNotification = false;

    public List<CellRowDB> cellRows(){
        return getMany(CellRowDB.class, "Controller");
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setName(String name) {
        this.name = name;
        save();
    }

    public boolean showNotification() {
        return showNotification;
    }

    public void showNotification(boolean showNotification) {
        this.showNotification = showNotification;
    }

    public CellRowDB addRow(){
        CellRowDB cellRow = new CellRowDB();
        cellRow.controller = this;
        cellRow.save();

        CellDB cell = new CellDB();
        cell.cellRow = cellRow;
        cell.save();

        return cellRow;
    }


    public static List<ControllerDB> getControllers(){
        return new Select()
                .from(ControllerDB.class)
                .execute();
    }

    /**
     * Delete controller and hide notification.
     *
     * @param context
     */
    public void deleteController(Context context){
        ControlHelper.hideNotification(context, this);
        delete();
    }

    @Override
    public String toString() {
        return name;
    }
}
