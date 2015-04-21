package treehou.se.habit.ui.control.config;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.Item;
import treehou.se.habit.core.Server;
import treehou.se.habit.core.controller.Cell;
import treehou.se.habit.core.controller.SliderCell;
import treehou.se.habit.ui.control.Icon;
import treehou.se.habit.ui.control.IconAdapter;

public class CellSliderConfigFragment extends Fragment {

    private static final String TAG = "CellSwitchConfigFragment";
    private static String ARG_CELL_ID = "ARG_CELL_ID";

    private Cell cell;

    private SliderCell numberCell;
    private Spinner sprItems;
    private TextView txtMax;
    private ArrayAdapter<Item> mItemAdapter ;
    private ArrayList<Item> mItems = new ArrayList<>();

    public static CellSliderConfigFragment newInstance(Cell cell) {
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

        if (getArguments() != null) {
            Long id = getArguments().getLong(ARG_CELL_ID);
            cell = Cell.load(Cell.class, id);
            if((numberCell =cell.sliderCell())==null){
                numberCell = new SliderCell();
                numberCell.setCell(cell);
                numberCell.save();
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_cell_number_config, container, false);

        final View louRange = rootView.findViewById(R.id.lou_range);

        sprItems = (Spinner) rootView.findViewById(R.id.spr_items);
        sprItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Item item = (Item) sprItems.getItemAtPosition(position);
                if(item != null) {
                    item.save();

                    if(item.getType().equals(Item.TYPE_NUMBER) || item.getType().equals(Item.TYPE_GROUP)){
                        louRange.setVisibility(View.VISIBLE);
                    }else {
                        louRange.setVisibility(View.GONE);
                    }

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
        List<Server> servers = Server.getServers();
        mItems.clear();
        if(numberCell.getItem() != null) {
            mItems.add(numberCell.getItem());
        }
        for(Server server : servers) {
            communicator.requestItems(server, new Communicator.ItemsRequestListener() {
                @Override
                public void onSuccess(List<Item> items) {
                    items = filterItems(items);
                    mItems.addAll(items);
                    mItemAdapter.notifyDataSetChanged();

                    int position = mItems.indexOf(numberCell.item);
                    if(position != -1){
                        sprItems.setSelection(position);
                    }
                }

                @Override
                public void onFailure(String message) {
                    Log.d("Get Items", "Failure " + message);
                }
            });
        }

        final Spinner sprIcons = (Spinner) rootView.findViewById(R.id.spr_icons);
        IconAdapter iconAdapter = new IconAdapter(getActivity());

        sprIcons.setAdapter(iconAdapter);
        Icon icon = new Icon();
        iconAdapter.getIndexOf(icon);
        icon.setValue(numberCell.getIcon());
        sprIcons.setSelection(iconAdapter.getIndexOf(icon));
        sprIcons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Icon icon = (Icon)sprIcons.getItemAtPosition(position);
                if(icon != null) {
                    numberCell.setIcon(icon.getValue());
                    numberCell.save();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

    private List<Item> filterItems(List<Item> items){

        List<Item> tempItems = new ArrayList<>();
        for(Item item : items){
            if(item.getType().equals(Item.TYPE_NUMBER)){
                tempItems.add(item);
            }else if(item.getType().equals(Item.TYPE_DIMMER)){
                tempItems.add(item);
            }else if(item.getType().equals(Item.TYPE_COLOR)){
                tempItems.add(item);
            }else if(item.getType().equals(Item.TYPE_GROUP)){
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

        if(numberCell.getItem().getType().equals(Item.TYPE_NUMBER)
                || numberCell.getItem().getType().equals(Item.TYPE_GROUP)){
            numberCell.setMin(0);
            numberCell.setMax(Integer.parseInt(txtMax.getText().toString()));
        }else{
            numberCell.setMin(0);
            numberCell.setMax(100);
        }

        numberCell.save();
    }
}
