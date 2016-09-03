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

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback;
import se.treehou.ng.ohcommunicator.services.callbacks.OHResponse;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.ItemDB;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.tasker.boundle.IncDecBoundleManager;
import treehou.se.habit.util.Constants;

public class IncDecActionFragment extends Fragment {

    private static final String TAG = "IncDecActionFragment";

    private Spinner sprItems;
    private TextView txtValue;
    private TextView txtMin;
    private TextView txtMax;

    private ArrayAdapter<OHItem> itemAdapter;
    private List<OHItem> filteredItems = new ArrayList<>();

    private Realm realm;

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
        realm = Realm.getDefaultInstance();
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
        sprItems.post(() -> sprItems.setAdapter(itemAdapter));
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

            IServerHandler serverHandler = new Connector.ServerHandler(server.toGeneric(), getActivity());
            serverHandler.requestItems(callback);
        }

        Button btnSave = (Button) rootView.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(v -> {
            save();
            getActivity().finish();
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
        for(OHItem item : items){
            if(Constants.SUPPORT_INC_DEC.contains(item.getType())){
                tempItems.add(item);
            }
        }
        items.clear();
        items.addAll(tempItems);

        return items;
    }

    public void save() {

        final Intent resultIntent = new Intent();

        // TODO Should probably add use the inc/dec command when it comes to dimmer item
        try {
            int value = Integer.parseInt(txtValue.getText().toString());
            int min = Integer.parseInt(txtMin.getText().toString());
            int max = Integer.parseInt(txtMax.getText().toString());

            OHItem item = (OHItem) sprItems.getSelectedItem();
            realm.beginTransaction();
            ItemDB itemDb = ItemDB.createOrLoadFromGeneric(realm, item);
            realm.commitTransaction();

            final Bundle resultBundle = IncDecBoundleManager.generateCommandBundle(getActivity(), itemDb.getId(), value, min, max);

            resultIntent.putExtra(treehou.se.habit.tasker.locale.Intent.EXTRA_STRING_BLURB, item.getName() + " - " + value);
            resultIntent.putExtra(treehou.se.habit.tasker.locale.Intent.EXTRA_BUNDLE, resultBundle);

            getActivity().setResult(Activity.RESULT_OK, resultIntent);

        }catch (NumberFormatException e){
            Log.e(TAG, "save", e);
        }
    }
}
