package treehou.se.habit.tasker.items;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import treehou.se.habit.tasker.boundle.CommandBoundleManager;

public class SwitchActionFragment extends Fragment {

    private Spinner sprItems;
    private ToggleButton tglOnOff;

    private ArrayAdapter<OHItemWrapper> itemAdapter;
    private List<OHItemWrapper> filteredItems = new ArrayList<>();

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
        List<OHServerWrapper> servers = OHServerWrapper.loadAll();
        filteredItems.clear();

        for(final OHServerWrapper server : servers) {
            OHCallback<List<OHItemWrapper>> callback = new OHCallback<List<OHItemWrapper>>() {
                @Override
                public void onUpdate(OHResponse<List<OHItemWrapper>> response) {
                    List<OHItemWrapper> items = filterItems(response.body());
                    filteredItems.addAll(items);
                    itemAdapter.notifyDataSetChanged();
                    Openhab.instance(server).deregisterItemsListener(this);
                }

                @Override
                public void onError() {

                }
            };

            Openhab.instance(server).registerItemsListener(callback);
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

    private List<OHItemWrapper> filterItems(List<OHItemWrapper> items){

        List<OHItemWrapper> tempItems = new ArrayList<>();
        for(OHItemWrapper item : items){
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

        OHItemWrapper item = (OHItemWrapper) sprItems.getSelectedItem();
        item.save();

        String command = tglOnOff.isChecked() ? Constants.COMMAND_ON : Constants.COMMAND_OFF;
        final Bundle resultBundle = CommandBoundleManager.generateCommandBundle(getActivity(), item, command);

        resultIntent.putExtra(treehou.se.habit.tasker.locale.Intent.EXTRA_STRING_BLURB, item.getName() + " - " + command);
        resultIntent.putExtra(treehou.se.habit.tasker.locale.Intent.EXTRA_BUNDLE, resultBundle);

        getActivity().setResult(Activity.RESULT_OK, resultIntent);
    }
}
