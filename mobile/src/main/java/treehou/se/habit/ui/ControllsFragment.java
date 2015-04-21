package treehou.se.habit.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import treehou.se.habit.R;
import treehou.se.habit.core.controller.Controller;

public class ControllsFragment extends Fragment {

    private static final String TAG = "ControllsFragment";

    private RecyclerView mListView;

    private ControllerAdapter mAdapter;

    public static ControllsFragment newInstance() {
        ControllsFragment fragment = new ControllsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ControllsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("ControllsFragment", ""+Controller.getControllers().size());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_controll, container, false);

        mListView = (RecyclerView) view.findViewById(R.id.list);

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.controllers);

        mAdapter = new ControllerAdapter(getActivity());
        final List<Controller> controllers = Controller.getControllers();
        mAdapter.addAll(controllers);

        // Set the adapter
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        mListView.setLayoutManager(gridLayoutManager);
        mListView.setItemAnimator(new SlideInLeftAnimator());
        mListView.setAdapter(mAdapter);

        setHasOptionsMenu(true);

        return view;
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
                Controller controller = new Controller();
                controller.name = "Controller ";
                Long id = controller.save();
                controller.name += getId();
                controller.save();
                controller.addRow();
                loadController(id);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public class ControllerAdapter extends RecyclerView.Adapter<ControllerAdapter.ControllerHolder>{

        private List<Controller> items = new ArrayList<>();
        private Context context;

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
        public void onBindViewHolder(ControllerHolder controllerHolder, final int position) {
            final Controller controller = items.get(position);

            controllerHolder.lblName.setText(controller.getName());

            controllerHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.page_container, ControlFragment.newInstance(controller.getId()))
                            .addToBackStack(null)
                            .commit();
                }
            });
            controllerHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(getActivity())
                        .setItems(R.array.controll_manager, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        loadController(controller.getId());
                                        break;
                                    case 1:
                                        mAdapter.removeItem(position);
                                        controller.delete();
                                        break;
                                }
                            }
                        }).create().show();

                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public Controller getItem(int position) {
            return items.get(position);
        }

        public void addItem(Controller controller) {
            items.add(0, controller);
            notifyItemInserted(0);
        }

        public void removeItem(int position) {
            Log.d(TAG, "removeItem: " + position);
            items.remove(position);
            notifyItemRemoved(position);
        }

        public void removeItem(Controller controller) {
            int position = items.indexOf(controller);
            items.remove(position);
            notifyItemRemoved(position);
        }

        public void addAll(List<Controller> controllers) {
            for(Controller controller : controllers) {
                items.add(0, controller);
                notifyItemRangeInserted(0, controllers.size());
            }
        }
    }
}
