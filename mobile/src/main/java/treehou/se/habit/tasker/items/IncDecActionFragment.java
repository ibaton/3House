package treehou.se.habit.tasker.items;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.trello.rxlifecycle2.components.support.RxFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.ItemDB;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.tasker.boundle.IncDecBoundleManager;
import treehou.se.habit.util.ConnectionFactory;
import treehou.se.habit.util.Constants;
import treehou.se.habit.util.Util;
import treehou.se.habit.util.logging.Logger;

public class IncDecActionFragment extends RxFragment {

    private static final String TAG = "IncDecActionFragment";

    private Spinner sprItems;
    private TextView txtValue;
    private TextView txtMin;
    private TextView txtMax;

    private ArrayAdapter<OHItem> itemAdapter;
    private List<OHItem> filteredItems = new ArrayList<>();

    @Inject ConnectionFactory connectionFactory;
    @Inject Logger logger;

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
        Util.INSTANCE.getApplicationComponent(this).inject(this);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tasker_inc_dec_action, container, false);

        sprItems = rootView.findViewById(R.id.itemsSpinner);
        txtValue = rootView.findViewById(R.id.valueText);
        txtMin = rootView.findViewById(R.id.minText);
        txtMax = rootView.findViewById(R.id.maxText);

        itemAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, filteredItems);
        sprItems.post(() -> sprItems.setAdapter(itemAdapter));
        RealmResults<ServerDB> servers = realm.where(ServerDB.class).findAll();
        filteredItems.clear();

        for (final ServerDB server : servers) {
            IServerHandler serverHandler = connectionFactory.createServerHandler(server.toGeneric(), getActivity());
            serverHandler.requestItemsRx()
                    .map(this::filterItems)
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(items -> {
                        filteredItems.addAll(items);
                        itemAdapter.notifyDataSetChanged();
                    }, throwable -> logger.e(TAG, "requestItemsRx failed", throwable));
        }

        Button btnSave = rootView.findViewById(R.id.btn_save);
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

    private List<OHItem> filterItems(List<OHItem> items) {

        List<OHItem> tempItems = new ArrayList<>();
        for (OHItem item : items) {
            if (Constants.INSTANCE.getSUPPORT_INC_DEC().contains(item.getType())) {
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
            ItemDB itemDb = ItemDB.Companion.createOrLoadFromGeneric(realm, item);
            realm.commitTransaction();

            final Bundle resultBundle = IncDecBoundleManager.Companion.generateCommandBundle(getActivity(), itemDb.getId(), value, min, max);

            resultIntent.putExtra(treehou.se.habit.tasker.locale.Intent.Companion.getEXTRA_STRING_BLURB(), item.getName() + " - " + value);
            resultIntent.putExtra(treehou.se.habit.tasker.locale.Intent.Companion.getEXTRA_BUNDLE(), resultBundle);

            getActivity().setResult(Activity.RESULT_OK, resultIntent);

        } catch (NumberFormatException e) {
            Log.e(TAG, "saveServer", e);
        }
    }
}
