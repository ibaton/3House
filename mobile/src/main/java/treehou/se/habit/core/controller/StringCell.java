package treehou.se.habit.core.controller;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by ibaton on 2014-11-08.
 */

@Table(name = "StringCells")
public class StringCell extends Model {

    @Column(name = "Cell", onDelete = Column.ForeignKeyAction.CASCADE)
    public Cell cell;

    @Column(name = "command")
    public String command;

    @Column(name = "icon")
    public int icon;

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

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
