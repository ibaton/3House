package treehou.se.habit.core.db.controller;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

@Table(name = "CellRows")
public class CellRowDB extends Model{

    private static final String TAG = "CellRow";

    @Column(name = "Controller", onDelete = Column.ForeignKeyAction.CASCADE)
    public ControllerDB controller;

    public List<CellDB> cells(){
        return getMany(CellDB.class, "CellRow");
    }

    public CellDB addCell(){

        Log.d(TAG, "Added cell to row " + getId());

        CellDB cell = new CellDB();
        cell.cellRow = this;
        cell.save();

        return cell;
    }

    public void deleteCell(CellDB cell){
        cell.delete();
        if(cells().size() == 0){
            delete();
        }
    }
}
