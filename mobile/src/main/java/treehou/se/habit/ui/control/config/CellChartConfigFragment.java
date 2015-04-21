package treehou.se.habit.ui.control.config;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import treehou.se.habit.R;
import treehou.se.habit.core.controller.Cell;
import treehou.se.habit.core.controller.ChartCell;

public class CellChartConfigFragment extends Fragment {

    private static String ARG_CELL_ID = "ARG_CELL_ID";

    public static CellChartConfigFragment newInstance(Cell cell) {
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
            Cell cell = Cell.load(Cell.class, id);
            ChartCell chartCell = cell.chartCell();

            if(chartCell ==null){
                chartCell = new ChartCell();
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
