package treehou.se.habit.core.controller;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by ibaton on 2014-11-08.
 */

@Table(name = "ChartCells")
public class ChartCell extends Model {

    @Column(name = "Cell", onDelete = Column.ForeignKeyAction.CASCADE)
    public Cell cell;

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }
}
