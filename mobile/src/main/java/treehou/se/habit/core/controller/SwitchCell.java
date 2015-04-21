package treehou.se.habit.core.controller;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by ibaton on 2014-11-08.
 */

@Table(name = "SwitchCells")
public class SwitchCell extends Model {

    @Column(name = "icon")
    public int iconOn;

    @Column(name = "Cell", onDelete = Column.ForeignKeyAction.CASCADE)
    public Cell cell;

    @Column(name = "command")
    public String command;

    public int getIconOn() {
        return iconOn;
    }

    public void setIconOn(int iconOn) {
        this.iconOn = iconOn;
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
