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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback;
import se.treehou.ng.ohcommunicator.services.callbacks.OHResponse;
import treehou.se.habit.R;
import treehou.se.habit.core.controller.Cell;
import treehou.se.habit.core.controller.SliderCell;
import treehou.se.habit.core.db.model.controller.CellDB;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.util.IconPickerActivity;

public class CellSliderConfigFragment extends Fragment {

    private static final String TAG = "CellSliderConfigFragment";
    
    private static String ARG_CELL_ID = "ARG_CELL_ID";
    private static int REQUEST_ICON = 183;

    private Cell cell;

    private SliderCell numberCell;
    private Spinner sprItems;
    private TextView txtMax;
    private ImageButton btnSetIcon;
    private View louRange;

    private ArrayAdapter<OHItem> mItemAdapter ;
    private ArrayList<OHItem> mItems = new ArrayList<>();

    public static CellSliderConfigFragment newInstance(CellDB cell) {
        CellSliderConfigFragment fragment = new CellSliderConfigFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CELL_ID, cell.getId());
        fragment.setArguments(args);
        return fragment;
    }

    public CellSliderConfigFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if (getArguments() != null) {
            int id = getArguments().getInt(ARG_CELL_ID);
            cell = new Cell(CellDB.load(id));
            SliderCellDB numberCellDb = SliderCellDB.getCell(cell.getDB());
            if(numberCellDb == null){
                numberCell = new SliderCell();
                numberCell.setCell(cell);
                numberCell.save();
            }
            else {
                numberCell = new SliderCell(numberCellDb);
            }
        }*/
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_cell_number_config, container, false);

        louRange = rootView.findViewById(R.id.lou_range);

        sprItems = (Spinner) rootView.findViewById(R.id.spr_items);
        sprItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                OHItem item = mItems.get(position);
                if (item != null) {
                    // TODO item.save();

                    if (item.getType().equals(OHItem.TYPE_NUMBER) || item.getType().equals(OHItem.TYPE_GROUP)) {
                        louRange.setVisibility(View.VISIBLE);
                    } else {
                        louRange.setVisibility(View.GONE);
                    }

                    numberCell.setItem(item);
                    numberCell.save();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mItemAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mItems);
        sprItems.post(new Runnable() {
            @Override
            public void run() {
                sprItems.setAdapter(mItemAdapter);
            }
        });
        List<OHServer> servers = null; // TODO OHServer.loadAll();
        mItems.clear();
        if(numberCell.getItem() != null) {
            mItems.add(numberCell.getItem());
        }
        for(final OHServer server : servers) {
            OHCallback<List<OHItem>> callback = new OHCallback<List<OHItem>>() {
                @Override
                public void onUpdate(OHResponse<List<OHItem>> response) {
                    List<OHItem> items = filterItems(response.body());
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

        txtMax = (TextView) rootView.findViewById(R.id.txt_max);
        if(numberCell != null){
            txtMax.setText(""+numberCell.getMax());
        }else{
            txtMax.setText(""+100);
        }

        return rootView;
    }

    private void updateIconImage(){
        btnSetIcon.setImageDrawable(Util.getIconDrawable(getActivity(), numberCell.getIcon()));
    }

    private List<OHItem> filterItems(List<OHItem> items){

        List<OHItem> tempItems = new ArrayList<>();
        for(OHItem item : items){
            if(item.getType().equals(OHItem.TYPE_NUMBER)){
                tempItems.add(item);
            }else if(item.getType().equals(OHItem.TYPE_DIMMER)){
                tempItems.add(item);
            }else if(item.getType().equals(OHItem.TYPE_COLOR)){
                tempItems.add(item);
            }else if(item.getType().equals(OHItem.TYPE_GROUP)){
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

        if(numberCell.getItem() == null){
            return;
        }

        if(numberCell.getItem().getType().equals(OHItem.TYPE_NUMBER)
                || numberCell.getItem().getType().equals(OHItem.TYPE_GROUP)){
            numberCell.setMin(0);
            numberCell.setMax(Integer.parseInt(txtMax.getText().toString()));
        }else{
            numberCell.setMin(0);
            numberCell.setMax(100);
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
