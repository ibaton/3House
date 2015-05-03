package treehou.se.habit.ui.control.config;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import treehou.se.habit.R;
import treehou.se.habit.core.controller.Cell;
import treehou.se.habit.ui.colorpicker.ColorDialog;
import treehou.se.habit.ui.control.config.cells.CellColorConfigFragment;

public class ControllCellFragment extends Fragment implements ColorDialog.ColorDialogCallback {

    public static final String TAG = "ControllCellFragment";
    public static final String ARG_CELL_ID = "ARG_CELL_ID";
    public static final int REQUEST_COLOR = 3001;

    private Button btnPicker;

    private ArrayAdapter mTypeAdapter;
    private Cell cell;

    public static ControllCellFragment newInstance(long cellId) {
        ControllCellFragment fragment = new ControllCellFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CELL_ID, cellId);
        fragment.setArguments(args);

        return fragment;
    }
    public ControllCellFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            long cellId = getArguments().getLong(ARG_CELL_ID);
            cell = Cell.load(Cell.class, cellId);
        }

        String[] cellTypes = getResources().getStringArray(R.array.cell_types);
        mTypeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, cellTypes);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_controll_cell, container, false);
        final Spinner sprItems = (Spinner) rootView.findViewById(R.id.spr_items);
        sprItems.setAdapter(mTypeAdapter);
        sprItems.setOnItemSelectedListener(itemSelectListener);

        //txtLabel = (TextView) rootView.findViewById(R.id.txt_label);

        btnPicker = (Button) rootView.findViewById(R.id.btn_color_picker);
        btnPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorDialog dialog = ColorDialog.instance();
                dialog.setTargetFragment(ControllCellFragment.this, REQUEST_COLOR);
                getActivity().getSupportFragmentManager().beginTransaction()
                    .add(dialog, "colordialog")
                    .commit();
            }
        });
        Log.d(TAG,"Color is : " + cell.getColor());
        setColor(cell.getColor());

        int[] typeArray = getResources().getIntArray(R.array.cell_types_values);
        int index = 0;
        for(int i=0; i < typeArray.length; i++){
            if(typeArray[i] == cell.getType()){
                index = i;
                break;
            }
        }
        sprItems.setSelection(index);

        return rootView;
    }

    private AdapterView.OnItemSelectedListener itemSelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            int cellType = getResources().getIntArray(R.array.cell_types_values)[position];

            cell.setType(cellType);
            cell.save();

            Log.d(TAG, "item selected " + cellType + " " + position);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = null;
            switch (cell.getType()) {
                case Cell.TYPE_BUTTON :
                    Log.d(TAG,"Loading button fragment.");
                    fragment = CellButtonConfigFragment.newInstance(cell);
                   break;
                case Cell.TYPE_SLIDER:
                    Log.d(TAG,"Loading slider fragment.");
                    fragment = CellSliderConfigFragment.newInstance(cell);
                    break;
                case Cell.TYPE_VOICE:
                    Log.d(TAG,"Loading voice fragment.");
                    fragment = CellVoiceConfigFragment.newInstance(cell);
                    break;
                case Cell.TYPE_COLOR:
                    Log.d(TAG,"Loading color fragment.");
                    fragment = CellColorConfigFragment.newInstance(cell);
                    break;
                case Cell.TYPE_INC_DEC:
                    Log.d(TAG,"Loading IncDec fragment.");
                    fragment = CellIncDecConfigFragment.newInstance(cell);
                    break;
                default:
                    Log.d(TAG,"Loading empty fragment.");
                    Fragment currentFragment = fragmentManager.findFragmentById(R.id.lou_config_container);
                    if(currentFragment != null) {
                        fragmentManager.beginTransaction()
                                .remove(currentFragment)
                                .commit();
                    }
                    break;
            }

            if(fragment != null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.lou_config_container,fragment)
                        .commit();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    };

    @Override
    public void setColor(int color) {
        Log.d(TAG,"Color set: " + color);
        btnPicker.setBackgroundColor(color);
        cell.setColor(color);
        cell.save();
    }

    @Override
    public void onPause() {
        super.onPause();

        /*if(TextUtilstxtLabel.getText()){
                cell.getItem().getType().equals(Item.TYPE_STRING) ||
        }*/
    }
}
