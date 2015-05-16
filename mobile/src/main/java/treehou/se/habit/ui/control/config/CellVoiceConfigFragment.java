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

import java.util.ArrayList;
import java.util.List;

import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.Item;
import treehou.se.habit.core.Server;
import treehou.se.habit.core.controller.Cell;
import treehou.se.habit.core.controller.VoiceCell;
import treehou.se.habit.ui.control.Icon;
import treehou.se.habit.ui.control.IconAdapter;

public class CellVoiceConfigFragment extends Fragment {

    private static final String TAG = "CellVoiceConfigFragment";

    private static String ARG_CELL_ID = "ARG_CELL_ID";

    private VoiceCell voiceCell;
    private Spinner sprItems;

    private Cell cell;

    private ArrayAdapter<Item> mItemAdapter ;
    private ArrayList<Item> mItems = new ArrayList<>();

    public static CellVoiceConfigFragment newInstance(Cell cell) {
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
            cell = Cell.load(Cell.class, id);
            if((voiceCell=cell.voiceCell())==null){
                voiceCell = new VoiceCell();
                voiceCell.setCell(cell);
                voiceCell.setIcon(R.drawable.cell_play);
                voiceCell.save();
            }
        }
    }

    private List<Item> filterItems(List<Item> items){

        List<Item> tempItems = new ArrayList<>();
        for(Item item : items){
            if(item.getType().equals(Item.TYPE_STRING)){
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
        List<Server> servers = Server.getServers();
        mItems.clear();
        for(Server server : servers) {
            communicator.requestItems(server, new Communicator.ItemsRequestListener() {
                @Override
                public void onSuccess(List<Item> items) {
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
                Item item = (Item) sprItems.getItemAtPosition(position);
                item.save();

                voiceCell.setItem(item);
                voiceCell.save();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final Spinner sprIcons = (Spinner) rootView.findViewById(R.id.spr_icons);
        IconAdapter iconAdapter = new IconAdapter(getActivity());

        sprIcons.setAdapter(iconAdapter);
        Icon icon = new Icon();
        icon.setValue(voiceCell.getIcon());
        sprIcons.setSelection(iconAdapter.getIndexOf(icon));

        sprIcons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Icon icon = (Icon)sprIcons.getItemAtPosition(position);
                voiceCell.setIcon(icon.getValue());
                voiceCell.save();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rootView;
    }


}
