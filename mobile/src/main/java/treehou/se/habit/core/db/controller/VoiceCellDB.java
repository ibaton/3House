package treehou.se.habit.core.db.controller;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import treehou.se.habit.core.db.ItemDB;

@Table(name = "VoiceCells")
public class VoiceCellDB extends Model {

    @Column(name = "icon")
    public String icon;

    @Column(name = "Cell", onDelete = Column.ForeignKeyAction.CASCADE)
    public CellDB cell;

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

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public CellDB getCell() {
        return cell;
    }

    public void setCell(CellDB cell) {
        this.cell = cell;
    }

}
