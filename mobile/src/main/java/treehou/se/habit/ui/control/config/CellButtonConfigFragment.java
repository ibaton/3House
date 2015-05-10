package treehou.se.habit.ui.control.config;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mikepenz.iconics.typeface.IIcon;

import java.util.ArrayList;
import java.util.List;

import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.connector.Constants;
import treehou.se.habit.core.Item;
import treehou.se.habit.core.Server;
import treehou.se.habit.core.controller.ButtonCell;
import treehou.se.habit.core.controller.Cell;
import treehou.se.habit.ui.Util;
import treehou.se.habit.ui.control.IconAdapter;

public class CellButtonConfigFragment extends Fragment {

    private static final String TAG = "CellSwitchConfigFrag";
    private static String ARG_CELL_ID = "ARG_CELL_ID";

    private ButtonCell buttonCell;
    private Cell cell;

    private Spinner sprItems;
    private ToggleButton tglOnOff;
    private TextView txtCommand;
    private ImageView btnSetIcon;

    private ArrayAdapter<Item> mItemAdapter;
    private ArrayList<Item> mItems = new ArrayList<>();

    public static CellButtonConfigFragment newInstance(Cell cell) {
        CellButtonConfigFragment fragment = new CellButtonConfigFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CELL_ID, cell.getId());
        fragment.setArguments(args);
        return fragment;
    }

    public CellButtonConfigFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Long id = getArguments().getLong(ARG_CELL_ID);
            cell = Cell.load(Cell.class, id);
            if((buttonCell=cell.buttonCell())==null){
                buttonCell = new ButtonCell();
                buttonCell.setCell(cell);
                buttonCell.setCommand(Constants.COMMAND_ON);
                buttonCell.save();
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_cell_button_config, container, false);

        txtCommand = (EditText) rootView.findViewById(R.id.txt_command);
        tglOnOff = (ToggleButton) rootView.findViewById(R.id.tgl_on_off);

        sprItems = (Spinner) rootView.findViewById(R.id.spr_items);
        sprItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Item item = mItems.get(position);
                if(item != null) {
                    item.save();

                    buttonCell.setItem(item);
                    buttonCell.save();
                    if (item.getType().equals(Item.TYPE_STRING)) {
                        txtCommand.setVisibility(View.VISIBLE);
                        txtCommand.setInputType(InputType.TYPE_CLASS_TEXT);
                        tglOnOff.setVisibility(View.GONE);
                    } else if (item.getType().equals(Item.TYPE_NUMBER)) {
                        txtCommand.setVisibility(View.VISIBLE);
                        txtCommand.setInputType(InputType.TYPE_CLASS_NUMBER);
                        tglOnOff.setVisibility(View.GONE);
                    } else if (item.getType().equals(Item.TYPE_CONTACT)) {
                        txtCommand.setVisibility(View.GONE);
                        tglOnOff.setVisibility(View.VISIBLE);
                    } else {
                        txtCommand.setVisibility(View.GONE);
                        tglOnOff.setVisibility(View.VISIBLE);
                    }
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
        if(buttonCell.getItem() != null) {
            mItems.add(buttonCell.getItem());
        }
        for(Server server : servers) {
            communicator.requestItems(server, new Communicator.ItemsRequestListener() {
                @Override
                public void onSuccess(List<Item> items) {
                    items = filterItems(items);
                    mItems.addAll(items);
                    mItemAdapter.notifyDataSetChanged();

                    int position = mItems.indexOf(buttonCell.item);
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

        tglOnOff.setChecked(
                Constants.COMMAND_ON.equals(buttonCell.getCommand()) ||
                Constants.COMMAND_OPEN.equals(buttonCell.getCommand()));
        txtCommand.setText(buttonCell.getCommand());

        btnSetIcon = (ImageView) rootView.findViewById(R.id.btn_set_icon);
        updateIconImage();
        btnSetIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.crateIconSelected(getActivity(), new IconAdapter.IconSelectListener() {
                    @Override
                    public void iconSelected(final IIcon icon) {
                        if (icon != null) {
                            buttonCell.setIcon(icon.getName());
                            buttonCell.save();
                            updateIconImage();
                        }
                    }
                });
            }
        });

        return rootView;
    }

    private void updateIconImage(){
        btnSetIcon.setImageDrawable(Util.getIconDrawable(getActivity(), buttonCell.getIcon()));
    }

    private List<Item> filterItems(List<Item> items){

        List<Item> tempItems = new ArrayList<>();
        for(Item item : items){
            if(item.getType().equals(Item.TYPE_SWITCH) ||
               item.getType().equals(Item.TYPE_GROUP) ||
               item.getType().equals(Item.TYPE_STRING) ||
               item.getType().equals(Item.TYPE_NUMBER) ||
               item.getType().equals(Item.TYPE_CONTACT) ||
               item.getType().equals(Item.TYPE_COLOR)){
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

        if(buttonCell.getItem().getType().equals(Item.TYPE_STRING) ||
                buttonCell.getItem().getType().equals(Item.TYPE_NUMBER)){
            buttonCell.setCommand(txtCommand.getText().toString());
        }else if(buttonCell.getItem().getType().equals(Item.TYPE_CONTACT)){
            buttonCell.setCommand(tglOnOff.isChecked()?Constants.COMMAND_OPEN:Constants.COMMAND_CLOSE);
        }else {
            buttonCell.setCommand(tglOnOff.isChecked()?Constants.COMMAND_ON:Constants.COMMAND_OFF);
        }
        buttonCell.save();
    }
}
