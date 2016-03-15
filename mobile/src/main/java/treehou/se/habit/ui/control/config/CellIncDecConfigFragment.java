package treehou.se.habit.ui.control.config;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.core.OHItemWrapper;
import se.treehou.ng.ohcommunicator.core.OHServerWrapper;
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback;
import se.treehou.ng.ohcommunicator.services.callbacks.OHResponse;
import treehou.se.habit.R;
import treehou.se.habit.core.controller.Cell;
import treehou.se.habit.core.controller.IncDecCell;
import treehou.se.habit.core.db.controller.CellDB;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.util.IconPickerActivity;

public class CellIncDecConfigFragment extends Fragment {

    private static final String TAG = "CellIncDecConfigFragment";

    private static String ARG_CELL_ID = "ARG_CELL_ID";
    private static int REQUEST_ICON = 183;

    private Cell cell;

    private IncDecCell numberCell;
    private Spinner sprItems;
    private EditText txtMax;
    private EditText txtMin;
    private EditText txtValue;
    private ImageButton btnSetIcon;

    private ArrayAdapter<OHItemWrapper> mItemAdapter;
    private ArrayList<OHItemWrapper> mItems = new ArrayList<>();

    public static CellIncDecConfigFragment newInstance(CellDB cell) {
        CellIncDecConfigFragment fragment = new CellIncDecConfigFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CELL_ID, cell.getId());
        fragment.setArguments(args);
        return fragment;
    }

    public CellIncDecConfigFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            int id = getArguments().getInt(ARG_CELL_ID);
            cell = Cell.load(id);
            numberCell = IncDecCell.getCell(cell);
            if (numberCell == null) {
                numberCell = new IncDecCell();
                numberCell.setCell(cell);
                numberCell.save();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.inc_dec_controller_action, container, false);

        txtMax = (EditText) rootView.findViewById(R.id.txtMax);
        txtMax.setText("" + numberCell.getMax());

        txtMin = (EditText) rootView.findViewById(R.id.txtMin);
        txtMin.setText("" + numberCell.getMin());

        txtValue = (EditText) rootView.findViewById(R.id.txtValue);
        txtValue.setText("" + numberCell.getValue());

        sprItems = (Spinner) rootView.findViewById(R.id.spr_items);
        sprItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                OHItemWrapper item = mItems.get(position);
                if(item != null) {
                    item.save();

                    numberCell.setItem(item);
                    numberCell.save();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        mItemAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mItems);
        sprItems.post(new Runnable() {
            @Override
            public void run() {
                sprItems.setAdapter(mItemAdapter);
            }
        });
        List<OHServerWrapper> servers = OHServerWrapper.loadAll();
        mItems.clear();
        if(numberCell.getItem() != null) {
            mItems.add(numberCell.getItem());
        }
        for(final OHServerWrapper server : servers) {
            OHCallback<List<OHItemWrapper>> callback = new OHCallback<List<OHItemWrapper>>() {
                @Override
                public void onUpdate(OHResponse<List<OHItemWrapper>> response) {
                    List<OHItemWrapper> items = filterItems(response.body());
                    mItems.addAll(items);
                    mItemAdapter.notifyDataSetChanged();
                    Openhab.instance(server).deregisterItemsListener(this);
                }

                @Override
                public void onError() {

                }
            };

            Openhab.instance(server).registerItemsListener(callback);
        }

        btnSetIcon = (ImageButton) rootView.findViewById(R.id.btn_set_icon);
        updateIconImage();
        btnSetIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), IconPickerActivity.class);
                startActivityForResult(intent, REQUEST_ICON);
            }
        });

        return rootView;
    }

    private void updateIconImage(){
        btnSetIcon.setImageDrawable(Util.getIconDrawable(getActivity(), numberCell.getIcon()));
    }

    private List<OHItemWrapper> filterItems(List<OHItemWrapper> items){

        List<OHItemWrapper> tempItems = new ArrayList<>();
        for(OHItemWrapper item : items){
            if(treehou.se.habit.Constants.SUPPORT_INC_DEC.contains(item.getType())){
                tempItems.add(item);
            }
        }
        items.clear();
        items.addAll(tempItems);

        return items;
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            numberCell.setMax(Integer.parseInt(txtMax.getText().toString()));
        }catch (NumberFormatException e) {
            numberCell.setMax(100);
        }
        try {
            numberCell.setMin(Integer.parseInt(txtMin.getText().toString()));
        }catch (NumberFormatException e) {
            numberCell.setMin(0);
        }
        try {
            numberCell.setValue(Integer.parseInt(txtValue.getText().toString()));
        }catch (NumberFormatException e) {
            numberCell.setValue(1);
        }

        numberCell.save();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ICON &&
                resultCode == Activity.RESULT_OK &&
                data.hasExtra(IconPickerActivity.RESULT_ICON)){

            String iconName = data.getStringExtra(IconPickerActivity.RESULT_ICON);
            numberCell.setIcon(iconName.equals("") ? null : iconName);
            numberCell.save();
            updateIconImage();
        }
    }
}
