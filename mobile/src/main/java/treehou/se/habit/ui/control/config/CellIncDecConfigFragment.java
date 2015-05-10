package treehou.se.habit.ui.control.config;

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

import com.mikepenz.iconics.typeface.IIcon;

import java.util.ArrayList;
import java.util.List;

import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.Item;
import treehou.se.habit.core.Server;
import treehou.se.habit.core.controller.Cell;
import treehou.se.habit.core.controller.IncDecCell;
import treehou.se.habit.ui.Util;
import treehou.se.habit.ui.control.IconAdapter;

public class CellIncDecConfigFragment extends Fragment {

    private static final String TAG = "CellIncDecConfigFragment";
    private static String ARG_CELL_ID = "ARG_CELL_ID";

    private Cell cell;

    private IncDecCell numberCell;
    private Spinner sprItems;
    private EditText txtMax;
    private EditText txtMin;
    private EditText txtValue;
    private ImageButton btnSetIcon;

    private ArrayAdapter<Item> mItemAdapter ;
    private ArrayList<Item> mItems = new ArrayList<>();

    public static CellIncDecConfigFragment newInstance(Cell cell) {
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
            cell = Cell.load(Cell.class, id);
            if((numberCell =cell.incDecCell())==null){
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
                Item item = mItems.get(position);
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

        btnSetIcon = (ImageButton) rootView.findViewById(R.id.btn_set_icon);
        updateIconImage();
        btnSetIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.crateIconSelected(getActivity(), new IconAdapter.IconSelectListener() {
                    @Override
                    public void iconSelected(final IIcon icon) {
                        if(icon != null) {
                            numberCell.setIcon(icon.getName());
                            numberCell.save();
                            updateIconImage();
                        }
                    }
                });
            }
        });

        return rootView;
    }

    private void updateIconImage(){
        btnSetIcon.setImageDrawable(Util.getIconDrawable(getActivity(), numberCell.getIcon()));
    }

    private List<Item> filterItems(List<Item> items){

        List<Item> tempItems = new ArrayList<>();
        for(Item item : items){
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

        numberCell.setMax(Integer.parseInt(txtMax.getText().toString()));
        numberCell.setMin(Integer.parseInt(txtMin.getText().toString()));
        numberCell.setValue(Integer.parseInt(txtValue.getText().toString()));

        numberCell.save();
    }
}
