package treehou.se.habit.ui.control.config.cells;

import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;

import treehou.se.habit.core.db.controller.CellDB;
import treehou.se.habit.core.db.controller.ControllerDB;
import treehou.se.habit.ui.control.CellFactory;

public class ChartConfigCellBuilder implements CellFactory.CellBuilder {

    private static final String TAG = "ChartConfigCellBuilder";

    public View build(Context context, ControllerDB controller, CellDB cell){

        /*ChartCell chartCell = cell.chartCell();

        LayoutInflater inflater = LayoutInflater.from(context);
        View cellView = inflater.inflate(R.layout.cell_conf_chart, null);

        TextView lblChartName = (TextView) cellView.findViewById(R.id.lbl_chart_name);
        lblChartName.setText(cell.getItem().getName());*/

        return null;//cellView;
    }

    @Override
    public RemoteViews buildRemote(Context context, ControllerDB controller, CellDB cell) {
        return null;
    }
}
