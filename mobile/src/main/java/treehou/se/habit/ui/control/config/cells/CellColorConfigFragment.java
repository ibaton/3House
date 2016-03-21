package treehou.se.habit.ui.control.config.cells;

import android.graphics.PorterDuff;
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

import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback;
import se.treehou.ng.ohcommunicator.services.callbacks.OHResponse;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.controller.CellDB;
import treehou.se.habit.core.db.model.controller.ColorCellDB;
import treehou.se.habit.ui.control.IconAdapter;

public class CellColorConfigFragment extends Fragment {

    private static final String TAG = "CellColorConfigFragment";

    private static String ARG_CELL_ID = "ARG_CELL_ID";

    private CellDB cell;

    private ColorCellDB colorCell;
    private Spinner sprItems;

    private ArrayAdapter<OHItem> mItemAdapter ;
    private ArrayList<OHItem> mItems = new ArrayList<>();

    public static CellColorConfigFragment newInstance(CellDB cell) {
        CellColorConfigFragment fragment = new CellColorConfigFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CELL_ID, cell.getId());
        fragment.setArguments(args);
        return fragment;
    }

    public CellColorConfigFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mItemAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mItems);

        /*if (getArguments() != null) {
            int id = getArguments().getInt(ARG_CELL_ID);
            cell = CellDB.load(id);
            colorCell = ColorCellDB.getCell(cell);
            if(colorCell == null){
                colorCell = new ColorCellDB();
                colorCell.setCell(cell);
                ColorCellDB.save(colorCell);
            }
        }*/
    }

    private List<OHItem> filterItems(List<OHItem> items){

        List<OHItem> tempItems = new ArrayList<>();
        for(OHItem item : items){
            if(item.getType().equals(OHItem.TYPE_COLOR) ||
                    item.getType().equals(OHItem.TYPE_GROUP)){
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

        //ColorCellDB.save(colorCell);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_cell_color_config, container, false);
        rootView.getBackground().setColorFilter(cell.getColor(), PorterDuff.Mode.MULTIPLY);

        sprItems = (Spinner) rootView.findViewById(R.id.spr_items);
        sprItems.setAdapter(mItemAdapter);

        List<OHServer> servers = null; // TODO OHServer.loadAll();
        mItems.clear();
        for(final OHServer server : servers) {
            OHCallback<List<OHItem>> callback = new OHCallback<List<OHItem>>() {
                @Override
                public void onUpdate(OHResponse<List<OHItem>> response) {
                    List<OHItem> items = filterItems(response.body());
                    mItems.addAll(items);
                    mItemAdapter.notifyDataSetChanged();

                    int position = mItems.indexOf(colorCell.getItem());
                    if(position != -1){
                        sprItems.setSelection(position);
                    }
                    Openhab.instance(server).deregisterItemsListener(this);
                }

                @Override
                public void onError() {

                }
            };
            Openhab.instance(server).registerItemsListener(callback);
        }

        sprItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                OHItem item = (OHItem) sprItems.getItemAtPosition(position);
                // TODO item.save();

                // colorCell.setItem(item.getDB());
                //ColorCellDB.save(colorCell);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final Spinner sprIcons = (Spinner) rootView.findViewById(R.id.spr_icons);
        IconAdapter iconAdapter = new IconAdapter(getActivity());

        /*sprIcons.setAdapter(iconAdapter);
        sprIcons.setSelection(iconAdapter.getIndexOf(icon));

        sprIcons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                IIcon icon = (IIcon)sprIcons.getItemAtPosition(position);
                colorCell.setIcon(icon.getName());
                colorCell.save();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        return rootView;
    }


}
