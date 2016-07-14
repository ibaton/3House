package treehou.se.habit.ui.control;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.controller.ControllerDB;
import treehou.se.habit.ui.adapter.ControllerAdapter;

/**
 * Fragment listing all app controllers.
 */
public class ControllsFragment extends Fragment {

    private static final String TAG = "ControllsFragment";

    private ControllerAdapter mAdapter;

    @BindView(R.id.list) RecyclerView mListView;
    @BindView(R.id.empty) View viwEmpty;
    @BindView(R.id.fab_add) FloatingActionButton fabAdd;

    private Realm realm;
    private Unbinder unbinder;

    public static ControllsFragment newInstance() {
        ControllsFragment fragment = new ControllsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ControllsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control_universal, container, false);
        unbinder = ButterKnife.bind(this, view);

        viwEmpty.setOnClickListener(view1 -> createNewController());
        fabAdd.setOnClickListener(v -> createNewController());
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(R.string.controllers);
        }

        mAdapter = new ControllerAdapter(getActivity());
        mAdapter.setItemListener(new ControllerAdapter.ItemListener() {
            @Override
            public void itemCountUpdated(int itemCount) {
                updateEmptyView(itemCount);
            }

            @Override
            public void itemClickListener(ControllerAdapter.ControllerHolder controllerHolder) {
                ControllerDB controller = mAdapter.getItem(controllerHolder.getAdapterPosition());
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.page_container, ControlFragment.newInstance(controller.getId()))
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public boolean itemLongClickListener(final ControllerAdapter.ControllerHolder controllerHolder) {
                final ControllerDB controller = mAdapter.getItem(controllerHolder.getAdapterPosition());
                new AlertDialog.Builder(getActivity())
                        .setItems(R.array.controll_manager, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        loadController(controller.getId());
                                        break;
                                    case 1:
                                        mAdapter.removeItem(controllerHolder.getAdapterPosition());

                                        final long id = controller.getId();
                                        realm.executeTransactionAsync(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                ControllerDB controller = ControllerDB.load(realm, id);
                                                controller.deleteFromRealm();
                                            }
                                        });
                                        break;
                                }
                            }
                        }).create().show();
                return true;
            }
        });
        final List<ControllerDB> controllers = realm.where(ControllerDB.class).findAll();
        mAdapter.addAll(controllers);

        // Set the adapter
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        mListView.setLayoutManager(gridLayoutManager);
        mListView.setItemAnimator(new DefaultItemAnimator());
        mListView.setAdapter(mAdapter);

        updateEmptyView(controllers.size());

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    /**
     * Show empty view if no controllers exist
     */
    private void updateEmptyView(int itemCount){
        viwEmpty.setVisibility(itemCount <= 0 ? View.VISIBLE : View.GONE);
    }

    public void loadController(long id){
        getActivity().getSupportFragmentManager().beginTransaction()
            .replace(R.id.page_container, EditControlFragment.newInstance(id))
            .addToBackStack(null)
            .commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.controllers, menu);
    }

    /**
     * Creates a new empty controller.
     */
    private void createNewController(){
        ControllerDB controller = new ControllerDB();
        String name = "Controller";
        controller.setName(name);
        ControllerDB.save(realm, controller);
        controller.addRow(realm);

        loadController(controller.getId());
    }
}
