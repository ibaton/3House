package treehou.se.habit.ui.control.config;

import android.os.Bundle;
import android.support.annotation.ColorInt;
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

import io.realm.Realm;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.controller.CellDB;
import treehou.se.habit.ui.colorpicker.ColorDialog;
import treehou.se.habit.ui.control.config.cells.CellColorConfigFragment;

public class ControllCellFragment extends Fragment implements ColorDialog.ColorDialogCallback {

    public static final String TAG = "ControllCellFragment";
    public static final String ARG_CELL_ID = "ARG_CELL_ID";
    public static final int REQUEST_COLOR = 3001;

    private Button btnPicker;

    private ArrayAdapter mTypeAdapter;
    private CellDB cell;

    private Realm realm;

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

        realm = Realm.getDefaultInstance();
        if (getArguments() != null) {
            long cellId = getArguments().getLong(ARG_CELL_ID);
            cell = CellDB.load(realm, cellId);
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

        btnPicker = (Button) rootView.findViewById(R.id.btn_color_picker);
        updateColorButton(cell.getColor());
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        realm.close();
    }

    private AdapterView.OnItemSelectedListener itemSelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            int cellType = getResources().getIntArray(R.array.cell_types_values)[position];

            realm.beginTransaction();
            cell.setType(cellType);
            realm.commitTransaction();

            Log.d(TAG, "item selected " + cellType + " " + position);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment fragment = null;
            switch (cell.getType()) {
                case CellDB.TYPE_BUTTON :
                    Log.d(TAG,"Loading button fragment.");
                    fragment = CellButtonConfigFragment.newInstance(cell);
                   break;
                case CellDB.TYPE_SLIDER:
                    Log.d(TAG,"Loading slider fragment.");
                    fragment = CellSliderConfigFragment.newInstance(cell);
                    break;
                case CellDB.TYPE_VOICE:
                    Log.d(TAG,"Loading voice fragment.");
                    fragment = CellVoiceConfigFragment.newInstance(cell);
                    break;
                case CellDB.TYPE_COLOR:
                    Log.d(TAG,"Loading color fragment.");
                    fragment = CellColorConfigFragment.newInstance(cell);
                    break;
                case CellDB.TYPE_INC_DEC:
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

    /**
     * Update the color of color button
     * @param color the color to set
     */
    public void updateColorButton(@ColorInt int color){
        btnPicker.setBackgroundColor(color);
    }

    @Override
    public void setColor(int color) {
        Log.d(TAG,"Color set: " + color);
        updateColorButton(color);

        realm.beginTransaction();
        cell.setColor(color);
        realm.commitTransaction();
    }
}
