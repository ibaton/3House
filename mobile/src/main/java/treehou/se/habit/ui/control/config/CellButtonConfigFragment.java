package treehou.se.habit.ui.control.config;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
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

import java.util.ArrayList;
import java.util.List;

import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.core.OHItemWrapper;
import se.treehou.ng.ohcommunicator.core.OHServerWrapper;
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback;
import se.treehou.ng.ohcommunicator.services.callbacks.OHResponse;
import treehou.se.habit.R;
import treehou.se.habit.connector.Constants;
import treehou.se.habit.core.db.controller.CellDB;
import treehou.se.habit.core.db.controller.ButtonCellDB;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.util.IconPickerActivity;

public class CellButtonConfigFragment extends Fragment {
    
    private static final String TAG = "CellButtonConfigFragment";
    
    private static String ARG_CELL_ID = "ARG_CELL_ID";
    private static int REQUEST_ICON = 183;

    private ButtonCellDB buttonCell;
    private CellDB cell;

    private Spinner sprItems;
    private ToggleButton tglOnOff;
    private TextView txtCommand;
    private ImageView btnSetIcon;

    private ArrayAdapter<OHItemWrapper> mItemAdapter;
    private ArrayList<OHItemWrapper> mItems = new ArrayList<>();

    public static CellButtonConfigFragment newInstance(CellDB cell) {
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

        /*if (getArguments() != null) {
            int id = getArguments().getInt(ARG_CELL_ID);
            cell = CellDB.load(id);
            buttonCell = ButtonCellDB.getCell(cell);
            if (buttonCell == null){
                buttonCell = new ButtonCellDB();
                buttonCell.setCell(cell);
                buttonCell.setCommand(Constants.COMMAND_ON);
                ButtonCellDB.save(buttonCell);
            }
        }*/
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
                OHItemWrapper item = mItems.get(position);
                if(item != null) {
                    item.save();

                    buttonCell.setItem(item.getDB());
                    //ButtonCellDB.save(buttonCell);
                    switch (item.getType()) {
                        case OHItemWrapper.TYPE_STRING:
                            txtCommand.setVisibility(View.VISIBLE);
                            txtCommand.setInputType(InputType.TYPE_CLASS_TEXT);
                            tglOnOff.setVisibility(View.GONE);
                            break;
                        case OHItemWrapper.TYPE_NUMBER:
                            txtCommand.setVisibility(View.VISIBLE);
                            txtCommand.setInputType(InputType.TYPE_CLASS_NUMBER);
                            tglOnOff.setVisibility(View.GONE);
                            break;
                        case OHItemWrapper.TYPE_CONTACT:
                            txtCommand.setVisibility(View.GONE);
                            tglOnOff.setVisibility(View.VISIBLE);
                            break;
                        default:
                            txtCommand.setVisibility(View.GONE);
                            tglOnOff.setVisibility(View.VISIBLE);
                            break;
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
        List<OHServerWrapper> servers = OHServerWrapper.loadAll();
        mItems.clear();

        if(buttonCell.getItem() != null) {
            mItems.add(new OHItemWrapper(buttonCell.getItem()));
        }

        if(buttonCell.getItem() != null) {
            mItems.add(new OHItemWrapper(buttonCell.getItem()));
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

        tglOnOff.setChecked(
                Constants.COMMAND_ON.equals(buttonCell.getCommand()) ||
                Constants.COMMAND_OPEN.equals(buttonCell.getCommand()));
        txtCommand.setText(buttonCell.getCommand());

        btnSetIcon = (ImageView) rootView.findViewById(R.id.btn_set_icon);
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
        btnSetIcon.setImageDrawable(Util.getIconDrawable(getActivity(), buttonCell.getIcon()));
    }

    private List<OHItemWrapper> filterItems(List<OHItemWrapper> items){

        List<OHItemWrapper> tempItems = new ArrayList<>();
        for(OHItemWrapper item : items){
            if(item.getType().equals(OHItemWrapper.TYPE_SWITCH) ||
               item.getType().equals(OHItemWrapper.TYPE_GROUP) ||
               item.getType().equals(OHItemWrapper.TYPE_STRING) ||
               item.getType().equals(OHItemWrapper.TYPE_NUMBER) ||
               item.getType().equals(OHItemWrapper.TYPE_CONTACT) ||
               item.getType().equals(OHItemWrapper.TYPE_COLOR)){
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

        if(buttonCell.getItem() == null) {
            buttonCell.setCommand("");
        } else if (buttonCell.getItem().getType().equals(OHItemWrapper.TYPE_STRING) || buttonCell.getItem().getType().equals(OHItemWrapper.TYPE_NUMBER)) {
            buttonCell.setCommand(txtCommand.getText().toString());
        } else if (buttonCell.getItem().getType().equals(OHItemWrapper.TYPE_CONTACT)) {
            buttonCell.setCommand(tglOnOff.isChecked() ? Constants.COMMAND_OPEN : Constants.COMMAND_CLOSE);
        } else {
            buttonCell.setCommand(tglOnOff.isChecked() ? Constants.COMMAND_ON : Constants.COMMAND_OFF);
        }
        //ButtonCellDB.save(buttonCell);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ICON &&
                resultCode == Activity.RESULT_OK &&
                data.hasExtra(IconPickerActivity.RESULT_ICON)){

            String iconName = data.getStringExtra(IconPickerActivity.RESULT_ICON);
            buttonCell.setIcon(iconName.equals("") ? null : iconName);
            //ButtonCellDB.save(buttonCell);
            updateIconImage();
        }
    }
}
