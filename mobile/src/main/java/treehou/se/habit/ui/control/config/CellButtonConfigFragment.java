package treehou.se.habit.ui.control.config;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback;
import se.treehou.ng.ohcommunicator.services.callbacks.OHResponse;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.R;
import treehou.se.habit.connector.Constants;
import treehou.se.habit.core.db.model.ItemDB;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.core.db.model.controller.ButtonCellDB;
import treehou.se.habit.core.db.model.controller.CellDB;
import treehou.se.habit.module.ServerLoaderFactory;
import treehou.se.habit.util.ConnectionFactory;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.util.IconPickerActivity;

public class CellButtonConfigFragment extends RxFragment {
    
    private static final String TAG = "CellButtonConfig";
    
    private static String ARG_CELL_ID = "ARG_CELL_ID";
    private static int REQUEST_ICON = 183;

    @BindView(R.id.spr_items) Spinner sprItems;
    @BindView(R.id.tgl_on_off) ToggleButton tglOnOff;
    @BindView(R.id.txt_command) TextView txtCommand;
    @BindView(R.id.btn_set_icon) ImageView btnSetIcon;

    @Inject ConnectionFactory connectionFactory;

    private ArrayAdapter<OHItem> mItemAdapter;
    private ArrayList<OHItem> mItems = new ArrayList<>();
    private OHItem item;
    private ButtonCellDB buttonCell;
    private CellDB cell;
    private Realm realm;
    private Unbinder unbinder;

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

        ((HabitApplication) getActivity().getApplication()).component().inject(this);

        realm = Realm.getDefaultInstance();

        if (getArguments() != null) {
            long id = getArguments().getLong(ARG_CELL_ID);
            cell = CellDB.load(realm, id);
            buttonCell = ButtonCellDB.getCell(realm, cell);

            if (buttonCell == null){
                buttonCell = new ButtonCellDB();
                buttonCell.setCell(cell);
                buttonCell.setCommand(Constants.COMMAND_ON);
                buttonCell = ButtonCellDB.save(realm, buttonCell);
            }

            ItemDB itemDB = buttonCell.getItem();
            if(itemDB != null){
                item = itemDB.toGeneric();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_cell_button_config, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        sprItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                realm.beginTransaction();
                OHItem item = mItems.get(position);
                if(item != null) {
                    ItemDB itemDB = ItemDB.createOrLoadFromGeneric(realm, item);
                    buttonCell.setItem(itemDB);
                    switch (item.getType()) {
                        case OHItem.TYPE_STRING:
                            txtCommand.setVisibility(View.VISIBLE);
                            txtCommand.setInputType(InputType.TYPE_CLASS_TEXT);
                            tglOnOff.setVisibility(View.GONE);
                            break;
                        case OHItem.TYPE_NUMBER:
                            txtCommand.setVisibility(View.VISIBLE);
                            txtCommand.setInputType(InputType.TYPE_CLASS_NUMBER);
                            tglOnOff.setVisibility(View.GONE);
                            break;
                        case OHItem.TYPE_CONTACT:
                            txtCommand.setVisibility(View.GONE);
                            tglOnOff.setVisibility(View.VISIBLE);
                            break;
                        default:
                            txtCommand.setVisibility(View.GONE);
                            tglOnOff.setVisibility(View.VISIBLE);
                            break;
                    }
                }
                realm.commitTransaction();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        mItemAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mItems);
        sprItems.setAdapter(mItemAdapter);
        List<ServerDB> servers = realm.where(ServerDB.class).findAll();
        mItems.clear();

        if(item != null){
            mItems.add(item);
            mItemAdapter.add(item);
            mItemAdapter.notifyDataSetChanged();
        }

        if(buttonCell.getItem() != null) {
            mItems.add(buttonCell.getItem().toGeneric());
        }
        for(final ServerDB serverDB : servers) {
            final OHServer server = serverDB.toGeneric();
            IServerHandler serverHandler = connectionFactory.createServerHandler(server, getActivity());
            serverHandler.requestItemsRx()
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(items -> {
                        mItems.addAll(items);
                        mItemAdapter.notifyDataSetChanged();
                    }, throwable -> {
                        Log.e(TAG, "Error fetching switch items");
                    });
        }

        tglOnOff.setChecked(
                Constants.COMMAND_ON.equals(buttonCell.getCommand()) ||
                Constants.COMMAND_OPEN.equals(buttonCell.getCommand()));
        txtCommand.setText(buttonCell.getCommand());

        updateIconImage();
        btnSetIcon.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), IconPickerActivity.class);
            startActivityForResult(intent, REQUEST_ICON);
        });

        return rootView;
    }

    private void updateIconImage(){
        btnSetIcon.setImageDrawable(Util.getIconDrawable(getActivity(), buttonCell.getIcon()));
    }

    private List<OHItem> filterItems(List<OHItem> items){

        List<OHItem> tempItems = new ArrayList<>();
        for(OHItem item : items){
            if(item.getType().equals(OHItem.TYPE_SWITCH) ||
               item.getType().equals(OHItem.TYPE_GROUP) ||
               item.getType().equals(OHItem.TYPE_STRING) ||
               item.getType().equals(OHItem.TYPE_NUMBER) ||
               item.getType().equals(OHItem.TYPE_CONTACT) ||
               item.getType().equals(OHItem.TYPE_COLOR)){
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
        if(buttonCell.getItem() == null) {
            buttonCell.setCommand("");
        } else if (buttonCell.getItem().getType().equals(OHItem.TYPE_STRING) || buttonCell.getItem().getType().equals(OHItem.TYPE_NUMBER)) {
            buttonCell.setCommand(txtCommand.getText().toString());
        } else if (buttonCell.getItem().getType().equals(OHItem.TYPE_CONTACT)) {
            buttonCell.setCommand(tglOnOff.isChecked() ? Constants.COMMAND_OPEN : Constants.COMMAND_CLOSE);
        } else {
            buttonCell.setCommand(tglOnOff.isChecked() ? Constants.COMMAND_ON : Constants.COMMAND_OFF);
        }
        realm.commitTransaction();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ICON &&
                resultCode == Activity.RESULT_OK &&
                data.hasExtra(IconPickerActivity.RESULT_ICON)){

            String iconName = data.getStringExtra(IconPickerActivity.RESULT_ICON);
            realm.beginTransaction();
            buttonCell.setIcon(iconName.equals("") ? null : iconName);
            realm.commitTransaction();
            updateIconImage();
        }
    }
}
