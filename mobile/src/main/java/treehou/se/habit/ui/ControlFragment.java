package treehou.se.habit.ui;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mattyork.colours.Colour;

import treehou.se.habit.R;
import treehou.se.habit.core.controller.Cell;
import treehou.se.habit.core.controller.CellRow;
import treehou.se.habit.core.controller.Controller;
import treehou.se.habit.ui.control.builders.ButtonCellBuilder;
import treehou.se.habit.ui.control.CellFactory;
import treehou.se.habit.ui.control.builders.EmptyCellBuilder;
import treehou.se.habit.ui.control.builders.IncDecCellBuilder;
import treehou.se.habit.ui.control.builders.SliderCellBuilder;
import treehou.se.habit.ui.control.builders.VoiceCellBuilder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ControlFragment extends Fragment {

    public static final String TAG = "ControlFragment";
    public static final String ARG_ID = "ARG_ID";

    private LinearLayout louController;

    private Controller controller;
    private CellFactory<Integer> cellFactory;

    private ActionBar actionBar;
    private AppCompatActivity activity;

    public static ControlFragment newInstance(long id) {
        ControlFragment fragment = new ControlFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    public ControlFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cellFactory = new CellFactory<>();
        cellFactory.setDefaultBuilder(new EmptyCellBuilder());
        cellFactory.addBuilder(Cell.TYPE_BUTTON, new ButtonCellBuilder());
        cellFactory.addBuilder(Cell.TYPE_INC_DEC, new IncDecCellBuilder());
        cellFactory.addBuilder(Cell.TYPE_SLIDER, new SliderCellBuilder());
        cellFactory.addBuilder(Cell.TYPE_VOICE, new VoiceCellBuilder());

        if (getArguments() != null) {
            Long id = getArguments().getLong(ARG_ID);
            controller = Controller.load(Controller.class, id);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activity = (AppCompatActivity) getActivity();
        actionBar = activity.getSupportActionBar();

        int[] pallete;
        if(Colour.alpha(controller.getColor()) < 100){
            pallete = Util.generatePallete(getResources().getColor(R.color.colorPrimary));
        }else{
            pallete = Util.generatePallete(controller.getColor());
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_contoll, container, false);

        View viwBackground = rootView.findViewById(R.id.viw_background);
        View titleHolder = rootView.findViewById(R.id.lou_title_holder);
        titleHolder.setBackgroundColor(pallete[0]);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            activity.getWindow().setStatusBarColor(pallete[0]);
            activity.getWindow().setNavigationBarColor(pallete[0]);
            if(actionBar != null) {
                actionBar.setBackgroundDrawable(new ColorDrawable(pallete[0]));
            }
        }

        viwBackground.setBackgroundColor(pallete[0]);

        TextView lblName = (TextView) rootView.findViewById(R.id.lbl_name);
        lblName.setText(controller.getName());
        louController = (LinearLayout) rootView.findViewById(R.id.lou_rows);
        redrawController();

        return rootView;
    }

    @Override
    public void onDestroyView() {

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
            activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.navigationBarColor));
            if(actionBar != null) {
                actionBar.setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(R.color.colorPrimary)));
            }
        }

        super.onDestroyView();
    }

    public void redrawController(){

        louController.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        for (final CellRow row : controller.cellRows()) {
            final LinearLayout louRow = (LinearLayout) inflater.inflate(R.layout.controller_row, null);
            LinearLayout.LayoutParams rowParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            rowParam.weight = 1;
            louRow.setLayoutParams(rowParam);

            final LinearLayout louColumnHolder = (LinearLayout) louRow.findViewById(R.id.lou_btn_holder);
            for (final Cell cell : row.cells()) {
                final View itemView = cellFactory.create(getActivity(), controller, cell);

                louColumnHolder.addView(itemView);
            }
            louController.addView(louRow);
        }
    }
}
