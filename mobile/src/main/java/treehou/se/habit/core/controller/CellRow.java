package treehou.se.habit.core.controller;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * Created by ibaton on 2014-11-03.
 */
@Table(name = "CellRows")
public class CellRow extends Model{

    private static final String TAG = "CellRow";

    @Column(name = "Controller", onDelete = Column.ForeignKeyAction.CASCADE)
    public Controller controller;

    public List<Cell> cells(){
        return getMany(Cell.class, "CellRow");
    }

    public Cell addCell(){

        Log.d(TAG, "Added cell to row " + getId());

        Cell cell = new Cell();
        cell.cellRow = this;
        cell.save();

        return cell;
    }

    public void deleteCell(Cell cell){
        cell.delete();
        if(cells().size() == 0){
            delete();
        }
    }
}
