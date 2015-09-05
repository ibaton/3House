package treehou.se.habit.ui.control;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import treehou.se.habit.R;
import treehou.se.habit.core.db.controller.ControllerDB;
import treehou.se.habit.ui.EditControlFragment;

/**
 * Fragment listing all app controllers.
 */
public class ControllsFragment extends Fragment {

    private static final String TAG = "ControllsFragment";

    private RecyclerView mListView;
    private ControllerAdapter mAdapter;

    private View viwEmpty;

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

        Log.d(TAG, ""+ ControllerDB.getControllers().size());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control, container, false);

        viwEmpty = view.findViewById(R.id.empty);
        viwEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewController();
            }
        });

        mListView = (RecyclerView) view.findViewById(R.id.list);

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
                                        controller.deleteController(getActivity());
                                        break;
                                }
                            }
                        }).create().show();
                return true;
            }
        });
        final List<ControllerDB> controllers = ControllerDB.getControllers();
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

    /**
     * Show empty view if no controllers exist
     */
    private void updateEmptyView(int itemCount){
        viwEmpty.setVisibility(itemCount <= 0 ? View.VISIBLE : View.GONE);
    }

    public void loadController(Long id){
        getActivity().getSupportFragmentManager().beginTransaction()
            .replace(R.id.page_container, EditControlFragment.newInstance(id))
            .addToBackStack(null)
            .commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.controllers, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add_controller:
                createNewController();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates a new empty controller.
     */
    private void createNewController(){
        ControllerDB controller = new ControllerDB();
        String name = "Controller";
        Long id = controller.save();
        name += getId();
        controller.setName(name);
        controller.save();
        controller.addRow();
        loadController(id);
    }

    public static class ControllerAdapter extends RecyclerView.Adapter<ControllerAdapter.ControllerHolder>{

        private List<ControllerDB> items = new ArrayList<>();
        private Context context;
        private ItemListener itemListener = new DummyItemListener();

        public class ControllerHolder extends RecyclerView.ViewHolder {
            public final TextView lblName;

            public ControllerHolder(View view) {
                super(view);
                lblName = (TextView) view.findViewById(R.id.lbl_controller);
            }
        }

        public ControllerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public ControllerHolder onCreateViewHolder(ViewGroup viewGroup, int position) {

            LayoutInflater inflater = LayoutInflater.from(context);
            View itemView = inflater.inflate(R.layout.item_controller, null);

            return new ControllerHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ControllerHolder controllerHolder, int position) {
            final ControllerDB controller = items.get(position);
            controllerHolder.lblName.setText(controller.getName());
            controllerHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.itemClickListener(controllerHolder);
                }
            });
            controllerHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return itemListener.itemLongClickListener(controllerHolder);
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        interface ItemListener {
            void itemCountUpdated(int itemCount);
            void itemClickListener(ControllerHolder controllerHolder);
            boolean itemLongClickListener(ControllerHolder controllerHolder);
        }

        class DummyItemListener implements ItemListener {

            @Override
            public void itemCountUpdated(int itemCount) {}

            @Override
            public void itemClickListener(ControllerHolder controllerHolder) {}

            @Override
            public boolean itemLongClickListener(ControllerHolder controllerHolder) {
                return false;
            }
        }

        public void setItemListener(ItemListener itemListener) {
            if(itemListener == null){
                this.itemListener = new DummyItemListener();
                return;
            }
            this.itemListener = itemListener;
        }

        public ControllerDB getItem(int position) {
            return items.get(position);
        }

        public void removeItem(int position) {
            Log.d(TAG, "removeItem: " + position);
            items.remove(position);
            notifyItemRemoved(position);
            itemListener.itemCountUpdated(items.size());
        }

        public void addItem(ControllerDB controller) {
            items.add(0, controller);
            notifyItemInserted(0);
            itemListener.itemCountUpdated(items.size());
        }

        public void addAll(List<ControllerDB> controllers) {
            for(ControllerDB controller : controllers) {
                items.add(0, controller);
                notifyItemRangeInserted(0, controllers.size());
            }
            itemListener.itemCountUpdated(items.size());
        }
    }
}
