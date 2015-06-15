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

import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.db.controller.CellDB;
import treehou.se.habit.core.db.ServerDB;
import treehou.se.habit.core.db.ItemDB;
import treehou.se.habit.core.db.controller.VoiceCellDB;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.util.IconPickerActivity;

public class CellVoiceConfigFragment extends Fragment {

    private static final String TAG = "CellVoiceConfigFragment";

    private static String ARG_CELL_ID = "ARG_CELL_ID";
    private static int REQUEST_ICON = 183;

    private VoiceCellDB voiceCell;
    private Spinner sprItems;
    private ImageButton btnSetIcon;

    private CellDB cell;

    private ArrayAdapter<ItemDB> mItemAdapter ;
    private ArrayList<ItemDB> mItems = new ArrayList<>();

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

        if (getArguments() != null) {
            Long id = getArguments().getLong(ARG_CELL_ID);
            cell = CellDB.load(CellDB.class, id);
            if((voiceCell=cell.voiceCell())==null){
                voiceCell = new VoiceCellDB();
                voiceCell.setCell(cell);
                voiceCell.save();
            }
        }
    }

    private List<ItemDB> filterItems(List<ItemDB> items){

        List<ItemDB> tempItems = new ArrayList<>();
        for(ItemDB item : items){
            if(item.getType().equals(ItemDB.TYPE_STRING)){
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

        Communicator communicator = Communicator.instance(getActivity());
        List<ServerDB> servers = ServerDB.getServers();
        mItems.clear();
        for(ServerDB server : servers) {
            communicator.requestItems(server, new Communicator.ItemsRequestListener() {
                @Override
                public void onSuccess(List<ItemDB> items) {
                    items = filterItems(items);
                    mItems.addAll(items);
                    mItemAdapter.notifyDataSetChanged();

                    int position = mItems.indexOf(voiceCell.item);
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

        sprItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ItemDB item = mItems.get(position);
                item.save();

                voiceCell.setItem(item);
                voiceCell.save();
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

            voiceCell.setIcon(data.getStringExtra(IconPickerActivity.RESULT_ICON));
            voiceCell.save();
            updateIconImage();
        }
    }
}
