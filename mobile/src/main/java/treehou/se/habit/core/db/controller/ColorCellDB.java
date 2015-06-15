package treehou.se.habit.core.db.controller;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import treehou.se.habit.core.db.ItemDB;

@Table(name = "ColorCells")
public class ColorCellDB extends Model {

    @Column(name = "iconName")
    public String icon;

    @Column(name = "Cell", onDelete = Column.ForeignKeyAction.CASCADE)
    public CellDB cell;

    @Column(name = "command")
    public String command;

    @Column(name = "Item")
    public ItemDB item;

    public ItemDB getItem() {
        return item;
    }

    public void setItem(ItemDB item) {
        this.item = item;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String iconOn) {
        this.icon = iconOn;
    }

    public CellDB getCell() {
        return cell;
    }

    public void setCell(CellDB cell) {
        this.cell = cell;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
