package treehou.se.habit.ui.control.config;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import treehou.se.habit.R;
import treehou.se.habit.core.db.controller.CellDB;
import treehou.se.habit.core.db.controller.ChartCellDB;

public class CellChartConfigFragment extends Fragment {

    private static String ARG_CELL_ID = "ARG_CELL_ID";

    public static CellChartConfigFragment newInstance(CellDB cell) {
        CellChartConfigFragment fragment = new CellChartConfigFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CELL_ID, cell.getId());
        fragment.setArguments(args);
        return fragment;
    }

    public CellChartConfigFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Long id = getArguments().getLong(ARG_CELL_ID);
            CellDB cell = CellDB.load(CellDB.class, id);
            ChartCellDB chartCell = cell.chartCell();

            if(chartCell ==null){
                chartCell = new ChartCellDB();
                chartCell.setCell(cell);
                chartCell.save();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_cell_chart_config, container, false);
    }

}
