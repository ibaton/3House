package treehou.se.habit.ui.control;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mattyork.colours.Colour;

import javax.inject.Inject;
import javax.inject.Named;

import io.realm.Realm;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.controller.CellDB;
import treehou.se.habit.core.db.model.controller.CellRowDB;
import treehou.se.habit.core.db.model.controller.ControllerDB;
import treehou.se.habit.util.Util;

public class ControlFragment extends Fragment {

    public static final String TAG = "ControlFragment";
    public static final String ARG_ID = "ARG_ID";

    private LinearLayout louController;

    private ControllerDB controller;
    @Inject @Named("display") CellFactory<Integer> cellFactory;

    private ActionBar actionBar;
    private AppCompatActivity activity;

    private Realm realm;

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
        ((HabitApplication)getContext().getApplicationContext()).component().inject(this);
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();
        if (getArguments() != null) {
            long id = getArguments().getLong(ARG_ID);
            controller = ControllerDB.load(realm, id);
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

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            activity.getWindow().setStatusBarColor(pallete[0]);
            activity.getWindow().setNavigationBarColor(pallete[0]);
            if(actionBar != null) {
                actionBar.setBackgroundDrawable(new ColorDrawable(pallete[0]));
            }
        }
        if(actionBar != null) {
            actionBar.setTitle(controller.getName());
        }

        viwBackground.setBackgroundColor(pallete[0]);

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

    @Override
    public void onDestroy() {
        super.onDestroy();

        realm.close();
    }

    public void redrawController(){

        louController.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        for (final CellRowDB row : controller.getCellRows()) {
            final LinearLayout louRow = (LinearLayout) inflater.inflate(R.layout.controller_row, null);
            LinearLayout.LayoutParams rowParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            rowParam.weight = 1;
            louRow.setLayoutParams(rowParam);

            final LinearLayout louColumnHolder = (LinearLayout) louRow.findViewById(R.id.lou_btn_holder);
            for (final CellDB cell : row.getCells()) {
                final View itemView = cellFactory.create(getActivity(), controller, cell);

                louColumnHolder.addView(itemView);
            }
            louController.addView(louRow);
        }
    }
}
