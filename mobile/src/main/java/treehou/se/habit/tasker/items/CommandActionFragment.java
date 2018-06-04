package treehou.se.habit.tasker.items;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.ItemDB;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.tasker.boundle.CommandBoundleManager;
import treehou.se.habit.util.ConnectionFactory;
import treehou.se.habit.util.Util;

public class CommandActionFragment extends RxFragment {

    private Spinner sprItems;
    private TextView txtCommand;

    private ArrayAdapter<OHItem> itemAdapter;
    private List<OHItem> filteredItems = new ArrayList<>();

    @Inject ConnectionFactory connectionFactory;

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
        Util.INSTANCE.getApplicationComponent(this).inject(this);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tasker_command_action, container, false);

        sprItems = (Spinner) rootView.findViewById(R.id.itemsSpinner);

        itemAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, filteredItems);
        sprItems.post(() -> sprItems.setAdapter(itemAdapter));
        RealmResults<ServerDB> servers = realm.where(ServerDB.class).findAll();
        filteredItems.clear();

        for(final ServerDB server : servers) {
            IServerHandler serverHandler = connectionFactory.createServerHandler(server.toGeneric(), getActivity());
            serverHandler.requestItemsRx()
                    .map(this::filterItems)
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(items -> {
                        filteredItems.addAll(items);
                        itemAdapter.notifyDataSetChanged();
                    });
        }

        txtCommand = (TextView) rootView.findViewById(R.id.commandText);

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

        List<OHItem> tempItems = new ArrayList<>(items);
        items.clear();
        items.addAll(tempItems);

        return items;
    }

    public void save() {

        final Intent resultIntent = new Intent();

        OHItem item = (OHItem) sprItems.getSelectedItem();
        realm.beginTransaction();
        ItemDB itemDb = ItemDB.Companion.createOrLoadFromGeneric(realm, item);
        realm.commitTransaction();

        String command = txtCommand.getText().toString();
        final Bundle resultBundle = CommandBoundleManager.Companion.generateCommandBundle(itemDb.getId(), command);

        resultIntent.putExtra(treehou.se.habit.tasker.locale.Intent.Companion.getEXTRA_STRING_BLURB(), item.getName() + " - " + command);
        resultIntent.putExtra(treehou.se.habit.tasker.locale.Intent.Companion.getEXTRA_BUNDLE(), resultBundle);

        getActivity().setResult(Activity.RESULT_OK, resultIntent);
    }
}
