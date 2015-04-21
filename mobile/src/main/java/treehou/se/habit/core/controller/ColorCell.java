package treehou.se.habit.core.controller;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import treehou.se.habit.R;
import treehou.se.habit.core.Item;

/**
 * Created by ibaton on 2014-11-08.
 */

@Table(name = "CollorCells")
public class ColorCell extends Model {

    @Column(name = "icon")
    public int icon;

    @Column(name = "Cell", onDelete = Column.ForeignKeyAction.CASCADE)
    public Cell cell;

    @Column(name = "command")
    public String command;

    @Column(name = "Item")
    public Item item;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getIcon() {
        return ((icon != 0) ? icon : R.drawable.cell_mute);
    }

    public void setIcon(int iconOn) {
        this.icon = iconOn;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
