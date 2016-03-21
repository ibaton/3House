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
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback;
import se.treehou.ng.ohcommunicator.services.callbacks.OHResponse;
import treehou.se.habit.R;
import treehou.se.habit.core.controller.Cell;
import treehou.se.habit.core.controller.VoiceCell;
import treehou.se.habit.core.db.model.controller.CellDB;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.util.IconPickerActivity;

public class CellVoiceConfigFragment extends Fragment {

    private static final String TAG = "CellVoiceConfigFragment";

    private static String ARG_CELL_ID = "ARG_CELL_ID";
    private static int REQUEST_ICON = 183;

    private VoiceCell voiceCell;
    private Spinner sprItems;
    private ImageButton btnSetIcon;

    private Cell cell;

    private ArrayAdapter<OHItem> mItemAdapter ;
    private ArrayList<OHItem> mItems = new ArrayList<>();

    public static CellVoiceConfigFragment newInstance(CellDB cell) {
        CellVoiceConfigFragment fragment = new CellVoiceConfigFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CELL_ID, cell.getId());
        fragment.setArguments(args);
        return fragment;
    }

    public CellVoiceConfigFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mItemAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mItems);

        /*if (getArguments() != null) {
            int id = getArguments().getInt(ARG_CELL_ID);
            cell = Cell.load(id);
            VoiceCellDB voiceCellDb = VoiceCellDB.getCell(cell.getDB());
            if(voiceCellDb == null){
                this.voiceCell = new VoiceCell();
                this.voiceCell.setCell(cell);
                voiceCell.save();
            }
            else {
                voiceCell = new VoiceCell(voiceCellDb);
            }
        }*/
    }

    private List<OHItem> filterItems(List<OHItem> items){

        List<OHItem> tempItems = new ArrayList<>();
        for(OHItem item : items){
            if(item.getType().equals(OHItem.TYPE_STRING)){
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

        Log.d(TAG, "onPause");

        voiceCell.save();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_cell_voice_config, container, false);

        sprItems = (Spinner) rootView.findViewById(R.id.spr_items);
        sprItems.setAdapter(mItemAdapter);

        List<OHServer> servers = null; //OHServer.loadAll();
        mItems.clear();

        if(voiceCell.getItem() != null) {
            // TODO mItems.add(new OHItem(voiceCell.getItem()));
        }

        for(final OHServer server : servers) {
            OHCallback<List<OHItem>> callback = new OHCallback<List<OHItem>>() {

                @Override
                public void onUpdate(OHResponse<List<OHItem>> response) {
                    List<OHItem> items = filterItems(response.body());
                    // TODO mItems.addAll(items);
                    mItemAdapter.notifyDataSetChanged();
                    Openhab.instance(server).deregisterItemsListener(this);
                }

                @Override
                public void onError() {
                    Log.d("Get Items", "Failure");
                }
            };
            Openhab.instance(server).registerItemsListener(callback);
        }

        sprItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* TODO  OHItem item = mItems.get(position);
                 item.save();

                voiceCell.setItem(item.getDB());
                voiceCell.save();*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

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
        btnSetIcon.setImageDrawable(Util.getIconDrawable(getActivity(), voiceCell.getIcon()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ICON &&
                resultCode == Activity.RESULT_OK &&
                data.hasExtra(IconPickerActivity.RESULT_ICON)){

            String iconName = data.getStringExtra(IconPickerActivity.RESULT_ICON);
            voiceCell.setIcon(iconName.equals("") ? null : iconName);
            voiceCell.save();
            updateIconImage();
        }
    }
}
