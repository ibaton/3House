package treehou.se.habit.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import treehou.se.habit.R;
import treehou.se.habit.core.db.model.controller.ControllerDB;
import treehou.se.habit.ui.control.ControllsFragment;

public class ControllerAdapter extends RecyclerView.Adapter<ControllerAdapter.ControllerHolder> {

    private static final String TAG = ControllerAdapter.class.getSimpleName();

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

    public interface ItemListener {
        void itemCountUpdated(int itemCount);

        void itemClickListener(ControllerHolder controllerHolder);

        boolean itemLongClickListener(ControllerHolder controllerHolder);
    }

    class DummyItemListener implements ItemListener {

        @Override
        public void itemCountUpdated(int itemCount) {
        }

        @Override
        public void itemClickListener(ControllerHolder controllerHolder) {
        }

        @Override
        public boolean itemLongClickListener(ControllerHolder controllerHolder) {
            return false;
        }
    }

    public void setItemListener(ItemListener itemListener) {
        if (itemListener == null) {
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
        for (ControllerDB controller : controllers) {
            items.add(0, controller);
            notifyItemRangeInserted(0, controllers.size());
        }
        itemListener.itemCountUpdated(items.size());
    }
}
