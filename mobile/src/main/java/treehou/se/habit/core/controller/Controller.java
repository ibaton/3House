package treehou.se.habit.core.controller;

import android.graphics.Color;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by ibaton on 2014-11-01.
 */

@Table(name = "Controllers")
public class Controller extends Model {

    @Column(name = "Name")
    public String name;

    @Column(name = "color")
    public int color = Color.parseColor("#33000000");

    public List<CellRow> cellRows(){
        return getMany(CellRow.class, "Controller");
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

    public CellRow addRow(){
        CellRow cellRow = new CellRow();
        cellRow.controller = this;
        cellRow.save();

        Cell cell = new Cell();
        cell.cellRow = cellRow;
        cell.save();

        return cellRow;
    }


    public static List<Controller> getControllers(){
        return new Select()
                .from(Controller.class)
                .execute();
    }

    @Override
    public String toString() {
        return name;
    }
}
