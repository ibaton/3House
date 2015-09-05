package treehou.se.habit.ui.control.config;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.db.controller.CellDB;
import treehou.se.habit.core.db.ItemDB;
import treehou.se.habit.core.db.ServerDB;
import treehou.se.habit.core.db.controller.IncDecCellDB;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.util.IconPickerActivity;

public class CellIncDecConfigFragment extends Fragment {

    private static final String TAG = "CellIncDecConfigFragment";

    private static String ARG_CELL_ID = "ARG_CELL_ID";
    private static int REQUEST_ICON = 183;

    private CellDB cell;

    private IncDecCellDB numberCell;
    private Spinner sprItems;
    private EditText txtMax;
    private EditText txtMin;
    private EditText txtValue;
    private ImageButton btnSetIcon;

    private ArrayAdapter<ItemDB> mItemAdapter ;
    private ArrayList<ItemDB> mItems = new ArrayList<>();

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
            Long id = getArguments().getLong(ARG_CELL_ID);
            cell = CellDB.load(CellDB.class, id);
            if((numberCell =cell.incDecCell())==null){
                numberCell = new IncDecCellDB();
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
                ItemDB item = mItems.get(position);
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
        Communicator communicator = Communicator.instance(getActivity());
        List<ServerDB> servers = ServerDB.getServers();
        mItems.clear();
        if(numberCell.getItem() != null) {
            mItems.add(numberCell.getItem());
        }
        for(ServerDB server : servers) {
            communicator.requestItems(server, new Communicator.ItemsRequestListener() {
                @Override
                public void onSuccess(List<ItemDB> items) {
                    items = filterItems(items);
                    mItems.addAll(items);
                    mItemAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(String message) {
                    Log.d("Get Items", "Failure " + message);
                }
            });
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

    private List<ItemDB> filterItems(List<ItemDB> items){

        List<ItemDB> tempItems = new ArrayList<>();
        for(ItemDB item : items){
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

            numberCell.setIcon(data.getStringExtra(IconPickerActivity.RESULT_ICON));
            numberCell.save();
            updateIconImage();
        }
    }
}
