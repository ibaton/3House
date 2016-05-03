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

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback;
import se.treehou.ng.ohcommunicator.services.callbacks.OHResponse;
import treehou.se.habit.R;
import treehou.se.habit.core.controller.Cell;
import treehou.se.habit.core.db.model.ItemDB;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.core.db.model.controller.CellDB;
import treehou.se.habit.core.db.model.controller.IncDecCellDB;
import treehou.se.habit.util.Constants;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.util.IconPickerActivity;

public class CellIncDecConfigFragment extends Fragment {

    private static final String TAG = "CellIncDecConfigFragment";

    private static String ARG_CELL_ID = "ARG_CELL_ID";
    private static int REQUEST_ICON = 183;

    @Bind(R.id.spr_items) Spinner sprItems;
    @Bind(R.id.txtMax) EditText txtMax;
    @Bind(R.id.txtMin) EditText txtMin;
    @Bind(R.id.txtValue) EditText txtValue;
    @Bind(R.id.btn_set_icon) ImageButton btnSetIcon;

    private ArrayAdapter<OHItem> mItemAdapter;
    private ArrayList<OHItem> mItems = new ArrayList<>();

    private IncDecCellDB incDecCell;
    private Cell cell;
    private OHItem item;
    private Realm realm;

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

        realm = Realm.getDefaultInstance();

        if (getArguments() != null) {
            long id = getArguments().getLong(ARG_CELL_ID);
            cell = new Cell(CellDB.load(realm, id));
            incDecCell = IncDecCellDB.getCell(realm, cell.getDB());

            if (incDecCell == null) {
                realm.beginTransaction();
                incDecCell = new IncDecCellDB();
                incDecCell.setId(IncDecCellDB.getUniqueId(realm));
                incDecCell = realm.copyToRealm(incDecCell);
                incDecCell.setCell(cell.getDB());
                realm.commitTransaction();
            }

            ItemDB itemDB = incDecCell.getItem();
            if(itemDB != null){
                item = itemDB.toGeneric();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.inc_dec_controller_action, container, false);
        ButterKnife.bind(this, rootView);

        txtMax.setText("" + incDecCell.getMax());
        txtMin.setText("" + incDecCell.getMin());
        txtValue.setText("" + incDecCell.getValue());

        sprItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                realm.beginTransaction();
                OHItem item = mItems.get(position);
                if(item != null) {
                    ItemDB itemDB = ItemDB.createOrLoadFromGeneric(realm, item);
                    incDecCell.setItem(itemDB);
                }
                realm.commitTransaction();
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
        List<ServerDB> servers = realm.allObjects(ServerDB.class);
        mItems.clear();

        if(item != null){
            mItems.add(item);
            mItemAdapter.add(item);
            mItemAdapter.notifyDataSetChanged();
        }

        if(incDecCell.getItem() != null) {
            //mItems.add(incDecCell.getItem());
        }
        for(final ServerDB serverDB : servers) {
            final OHServer server = serverDB.toGeneric();
            OHCallback<List<OHItem>> callback = new OHCallback<List<OHItem>>() {
                @Override
                public void onUpdate(OHResponse<List<OHItem>> response) {
                    List<OHItem> items = filterItems(response.body());
                    mItems.addAll(items);
                    mItemAdapter.notifyDataSetChanged();
                }

                @Override
                public void onError() {

                }
            };

            Openhab.instance(server).requestItem(callback);
        }

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
        btnSetIcon.setImageDrawable(Util.getIconDrawable(getActivity(), incDecCell.getIcon()));
    }

    private List<OHItem> filterItems(List<OHItem> items){

        List<OHItem> tempItems = new ArrayList<>();
        for(OHItem item : items){
            if(Constants.SUPPORT_INC_DEC.contains(item.getType())){
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

        realm.beginTransaction();
        try {
            incDecCell.setMax(Integer.parseInt(txtMax.getText().toString()));
        }catch (NumberFormatException e) {
            incDecCell.setMax(100);
        }
        try {
            incDecCell.setMin(Integer.parseInt(txtMin.getText().toString()));
        }catch (NumberFormatException e) {
            incDecCell.setMin(0);
        }
        try {
            incDecCell.setValue(Integer.parseInt(txtValue.getText().toString()));
        }catch (NumberFormatException e) {
            incDecCell.setValue(1);
        }
        realm.commitTransaction();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        realm.close();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ICON &&
                resultCode == Activity.RESULT_OK &&
                data.hasExtra(IconPickerActivity.RESULT_ICON)){

            String iconName = data.getStringExtra(IconPickerActivity.RESULT_ICON);
            realm.beginTransaction();
            incDecCell.setIcon(iconName.equals("") ? null : iconName);
            realm.commitTransaction();
            updateIconImage();
        }
    }
}
