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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback;
import se.treehou.ng.ohcommunicator.services.callbacks.OHResponse;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.ItemDB;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.tasker.boundle.CommandBoundleManager;

public class CommandActionFragment extends Fragment {

    private Spinner sprItems;
    private TextView txtCommand;

    private ArrayAdapter<OHItem> itemAdapter;
    private List<OHItem> filteredItems = new ArrayList<>();

    private Realm realm;

    public static CommandActionFragment newInstance() {
        CommandActionFragment fragment = new CommandActionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public CommandActionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tasker_command_action, container, false);

        sprItems = (Spinner) rootView.findViewById(R.id.spr_items);

        itemAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, filteredItems);
        sprItems.post(new Runnable() {
            @Override
            public void run() {
                sprItems.setAdapter(itemAdapter);
            }
        });
        RealmResults<ServerDB> servers = realm.where(ServerDB.class).findAll();
        filteredItems.clear();

        for(final ServerDB server : servers) {
            OHCallback<List<OHItem>> callback = new OHCallback<List<OHItem>>() {
                @Override
                public void onUpdate(OHResponse<List<OHItem>> response) {
                    List<OHItem> items = filterItems(response.body());
                    filteredItems.addAll(items);
                    itemAdapter.notifyDataSetChanged();
                }

                @Override
                public void onError() {

                }
            };

            Openhab.instance(server.toGeneric()).requestItem(callback);
        }

        txtCommand = (TextView) rootView.findViewById(R.id.txt_command);

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private List<OHItem> filterItems(List<OHItem> items){

        List<OHItem> tempItems = new ArrayList<>();
        tempItems.addAll(items);
        items.clear();
        items.addAll(tempItems);

        return items;
    }

    public void save() {

        final Intent resultIntent = new Intent();

        OHItem item = (OHItem) sprItems.getSelectedItem();
        realm.beginTransaction();
        ItemDB itemDb = ItemDB.createOrLoadFromGeneric(realm, item);
        realm.commitTransaction();

        String command = txtCommand.getText().toString();
        final Bundle resultBundle = CommandBoundleManager.generateCommandBundle(itemDb.getId(), command);

        resultIntent.putExtra(treehou.se.habit.tasker.locale.Intent.EXTRA_STRING_BLURB, item.getName() + " - " + command);
        resultIntent.putExtra(treehou.se.habit.tasker.locale.Intent.EXTRA_BUNDLE, resultBundle);

        getActivity().setResult(Activity.RESULT_OK, resultIntent);
    }
}
