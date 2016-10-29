package treehou.se.habit.ui.control.config.cells;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.controller.CellDB;
import treehou.se.habit.core.db.model.controller.ColorCellDB;
import treehou.se.habit.ui.adapter.IconAdapter;

public class CellColorConfigFragment extends RxFragment {

    private static final String TAG = "CellColorConfigFragment";

    private static String ARG_CELL_ID = "ARG_CELL_ID";

    private CellDB cell;

    private ColorCellDB colorCell;
    private Spinner sprItems;

    private ArrayAdapter<OHItem> itemAdapter;
    private ArrayList<OHItem> items = new ArrayList<>();

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

        itemAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);

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
        sprItems.setAdapter(itemAdapter);

        List<OHServer> servers = null; // TODO OHServer.loadAll();
        items.clear();
        for(final OHServer server : servers) {
            IServerHandler serverHandler = new Connector.ServerHandler(server, getContext());
            serverHandler.requestItemsRx()
                    .map(this::filterItems)
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(items -> {
                        this.items.addAll(items);
                        itemAdapter.notifyDataSetChanged();

                        int position = this.items.indexOf(colorCell.getItem());
                        if(position != -1){
                            sprItems.setSelection(position);
                        }
                    });
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
