package treehou.se.habit.ui.control;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import java.util.HashMap;
import java.util.Map;

import treehou.se.habit.R;
import treehou.se.habit.core.controller.Cell;
import treehou.se.habit.core.controller.Controller;

/**
 * Created by ibaton on 2014-11-08.
 */
public class CellFactory<T> {

    private static final String TAG = "CellFactory";

    private Map<T, CellBuilder> cellBuilders = new HashMap<>();
    private CellBuilder defaultBuilder = new DefaultBuilder();

    public void addBuilder(T type, CellBuilder builder){
        cellBuilders.put(type, builder);
    }

    public void setDefaultBuilder(CellBuilder builder){
        defaultBuilder = builder;
    }

    public View create(Context context, Controller controller, final Cell cell){

        Log.d(TAG,"cellBuilder cell type " + cell.getType());
        View cellView;
        try {
            int cellType = cell.getType();

            CellBuilder cellBuilder = cellBuilders.get(cellType);
            if (cellBuilder == null) {
                Log.d(TAG, "cellBuilder using default");
                cellBuilder = defaultBuilder;
            } else {
                Log.d(TAG, "cellBuilder using custom");
            }
            cellView = cellBuilder.build(context, controller, cell);
        }catch (Exception e){
            Log.d(TAG, "Failed render " + cell + " error " + e);
            e.printStackTrace();
            cellView = defaultBuilder.build(context, controller, cell);
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        cellView.setLayoutParams(lp);

        return cellView;
    }

    public RemoteViews createRemote(Context context, Controller controller, final Cell cell){

        Log.d(TAG,"cellBuilder cell type " + cell.getType());

        CellBuilder cellBuilder = cellBuilders.get(cell.getType());

        if(cellBuilder == null) {
            Log.d(TAG,"cellBuilder using default");
            cellBuilder  = defaultBuilder;
        }else{
            Log.d(TAG,"cellBuilder using custom");
        }

        RemoteViews remoteViews;
        try {
            remoteViews = cellBuilder.buildRemote(context, controller, cell);
        }catch (Exception e){
            remoteViews = defaultBuilder.buildRemote(context, controller, cell);
        }

        return remoteViews;
    }

    public static interface CellBuilder {

        public View build(Context context, Controller controller, Cell cell);
        public RemoteViews buildRemote(Context context, Controller controller, Cell cell);
    }

    public static class DefaultBuilder implements CellBuilder {

        public View build(Context context, Controller controller, Cell cell){

            LayoutInflater inflater = LayoutInflater.from(context);
            return inflater.inflate(R.layout.cell_empty, null);
        }

        @Override
        public RemoteViews buildRemote(Context context, Controller controller, Cell cell) {
            return new RemoteViews(context.getPackageName(), R.layout.cell_empty);
        }
    }
}
