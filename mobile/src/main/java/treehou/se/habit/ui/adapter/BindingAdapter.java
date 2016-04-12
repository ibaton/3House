package treehou.se.habit.ui.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import se.treehou.ng.ohcommunicator.connector.models.OHBinding;
import treehou.se.habit.R;
import treehou.se.habit.ui.BindingsFragment;

public class BindingAdapter extends RecyclerView.Adapter<BindingAdapter.BindingHolder> {

    private BindingsFragment bindingsFragment;
    private List<OHBinding> bindings = new ArrayList<>();
    private ItemClickListener itemClickListener;

    public class BindingHolder extends RecyclerView.ViewHolder {

        private TextView lblName;
        private TextView lblAuthor;
        private TextView lblDescription;

        public BindingHolder(View itemView) {
            super(itemView);

            lblName = (TextView) itemView.findViewById(R.id.lbl_name);
            lblAuthor = (TextView) itemView.findViewById(R.id.lbl_author);
            lblDescription = (TextView) itemView.findViewById(R.id.lbl_description);
        }
    }

    public BindingAdapter(BindingsFragment bindingsFragment) {
        this.bindingsFragment = bindingsFragment;
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(bindingsFragment.getActivity());
        View itemView = inflater.inflate(R.layout.item_binding, null);

        return new BindingHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final BindingHolder holder, int position) {

        final OHBinding binding = bindings.get(position);
        holder.lblName.setText(binding.getName());
        holder.lblAuthor.setText(binding.getAuthor());
        holder.lblDescription.setText(binding.getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(binding);
            }
        });
    }

    /**
     * Set listener listening for selections of bindings.
     * @param itemClickListener
     */
    public void setItemClickListener(ItemClickListener itemClickListener) {
        if(itemClickListener == null){
            itemClickListener = new DummyItemListener();
        }
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return bindings.size();
    }

    public void addBinding(OHBinding binding) {
        bindings.add(binding);
        notifyItemInserted(bindings.size() - 1);
    }

    public void setBindings(List<OHBinding> newBindings) {
        bindings.clear();
        bindings.addAll(newBindings);
        notifyDataSetChanged();
    }

    public interface ItemClickListener{
        void onClick(OHBinding binding);
    }

    private class DummyItemListener implements ItemClickListener {
        @Override
        public void onClick(OHBinding binding) {}
    }
}
