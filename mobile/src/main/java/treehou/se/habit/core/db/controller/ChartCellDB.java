package treehou.se.habit.core.db.controller;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "ChartCells")
public class ChartCellDB extends Model {

    @Column(name = "Cell", onDelete = Column.ForeignKeyAction.CASCADE)
    public CellDB cell;

    public CellDB getCell() {
        return cell;
    }

    public void setCell(CellDB cell) {
        this.cell = cell;
    }
}
