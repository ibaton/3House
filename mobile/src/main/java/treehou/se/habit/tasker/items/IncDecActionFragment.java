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
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.connector.Constants;
import treehou.se.habit.core.Item;
import treehou.se.habit.core.Server;
import treehou.se.habit.tasker.boundle.CommandBoundleManager;
import treehou.se.habit.tasker.boundle.IncDecBoundleManager;

public class IncDecActionFragment extends Fragment {

    private static final String TAG = "IncDecActionFragment";

    private Spinner sprItems;
    private TextView txtValue;
    private TextView txtMin;
    private TextView txtMax;

    private ArrayAdapter<Item> itemAdapter;
    private List<Item> filteredItems = new ArrayList<>();

    public static IncDecActionFragment newInstance() {
        IncDecActionFragment fragment = new IncDecActionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public IncDecActionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tasker_inc_dec_action, container, false);

        sprItems = (Spinner) rootView.findViewById(R.id.spr_items);
        txtValue = (TextView) rootView.findViewById(R.id.txtValue);
        txtMin = (TextView) rootView.findViewById(R.id.txtMin);
        txtMax = (TextView) rootView.findViewById(R.id.txtMax);

        itemAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, filteredItems);
        sprItems.post(new Runnable() {
            @Override
            public void run() {
                sprItems.setAdapter(itemAdapter);
            }
        });
        Communicator communicator = Communicator.instance(getActivity());
        List<Server> servers = Server.getServers();
        filteredItems.clear();

        for(Server server : servers) {
            communicator.requestItems(server, new Communicator.ItemsRequestListener() {
                @Override
                public void onSuccess(List<Item> items) {
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

    private List<Item> filterItems(List<Item> items){

        List<Item> tempItems = new ArrayList<>();
        for(Item item : items){
            if(treehou.se.habit.Constants.SUPPORT_INC_DEC.contains(item.getType())){
                tempItems.add(item);
            }
        }
        items.clear();
        items.addAll(tempItems);

        return items;
    }

    public void save() {

        final Intent resultIntent = new Intent();

        try {
            int value = Integer.parseInt(txtValue.getText().toString());
            int min = Integer.parseInt(txtMin.getText().toString());
            int max = Integer.parseInt(txtMax.getText().toString());

            Item item = (Item) sprItems.getSelectedItem();
            item.save();

            final Bundle resultBundle = IncDecBoundleManager.generateCommandBundle(getActivity(), item, value, min, max);

            resultIntent.putExtra(treehou.se.habit.tasker.locale.Intent.EXTRA_STRING_BLURB, item.getName() + " - " + value);
            resultIntent.putExtra(treehou.se.habit.tasker.locale.Intent.EXTRA_BUNDLE, resultBundle);

            getActivity().setResult(Activity.RESULT_OK, resultIntent);

        }catch (NumberFormatException e){
            Log.e(TAG, "save", e);
        }
    }
}
