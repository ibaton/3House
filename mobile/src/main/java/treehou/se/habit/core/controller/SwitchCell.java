package treehou.se.habit.core.controller;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by ibaton on 2014-11-08.
 */

@Table(name = "SwitchCells")
public class SwitchCell extends Model {

    @Column(name = "iconName")
    public String icon;

    @Column(name = "Cell", onDelete = Column.ForeignKeyAction.CASCADE)
    public Cell cell;

    @Column(name = "command")
    public String command;

    public String getIconOn() {
        return icon;
    }

    public void setIconOn(String icon) {
        this.icon = icon;
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
