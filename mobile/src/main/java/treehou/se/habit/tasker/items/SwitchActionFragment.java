package treehou.se.habit.tasker.items;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.connector.Constants;
import treehou.se.habit.core.db.ServerDB;
import treehou.se.habit.core.db.ItemDB;
import treehou.se.habit.tasker.boundle.CommandBoundleManager;

public class SwitchActionFragment extends Fragment {

    private Spinner sprItems;
    private ToggleButton tglOnOff;

    private ArrayAdapter<ItemDB> itemAdapter;
    private List<ItemDB> filteredItems = new ArrayList<>();

    public static SwitchActionFragment newInstance() {
        SwitchActionFragment fragment = new SwitchActionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SwitchActionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tasker_switch_action, container, false);

        sprItems = (Spinner) rootView.findViewById(R.id.spr_items);

        itemAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, filteredItems);
        sprItems.post(new Runnable() {
            @Override
            public void run() {
                sprItems.setAdapter(itemAdapter);
            }
        });
        Communicator communicator = Communicator.instance(getActivity());
        List<ServerDB> servers = ServerDB.getServers();
        filteredItems.clear();

        for(ServerDB server : servers) {
            communicator.requestItems(server, new Communicator.ItemsRequestListener() {
                @Override
                public void onSuccess(List<ItemDB> items) {
                    items = filterItems(items);
                    filteredItems.addAll(items);
                    itemAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(String message) {
                    Log.d("Get Items", "Failure " + message);
                }
            });
        }

        tglOnOff = (ToggleButton) rootView.findViewById(R.id.tgl_on_off);

        Button btnSave = (Button) rootView.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                getActivity().finish();
            }
        });

        return rootView;
    }

    private List<ItemDB> filterItems(List<ItemDB> items){

        List<ItemDB> tempItems = new ArrayList<>();
        for(ItemDB item : items){
            if(treehou.se.habit.Constants.SUPPORT_SWITCH.contains(item.getType())){
                tempItems.add(item);
            }
        }
        items.clear();
        items.addAll(tempItems);

        return items;
    }

    public void save() {

        final Intent resultIntent = new Intent();

        ItemDB item = (ItemDB) sprItems.getSelectedItem();
        item.save();

        String command = tglOnOff.isChecked() ? Constants.COMMAND_ON : Constants.COMMAND_OFF;
        final Bundle resultBundle = CommandBoundleManager.generateCommandBundle(getActivity(), item, command);

        resultIntent.putExtra(treehou.se.habit.tasker.locale.Intent.EXTRA_STRING_BLURB, item.getName() + " - " + command);
        resultIntent.putExtra(treehou.se.habit.tasker.locale.Intent.EXTRA_BUNDLE, resultBundle);

        getActivity().setResult(Activity.RESULT_OK, resultIntent);
    }
}
