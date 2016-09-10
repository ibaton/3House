package treehou.se.habit.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import se.treehou.ng.ohcommunicator.connector.models.OHLink;
import treehou.se.habit.R;

public class LinkAdapter extends RecyclerView.Adapter<LinkAdapter.LinkHolder> {

    private static String TAG = LinkAdapter.class.getSimpleName();

    private List<OHLink> items = new ArrayList<>();

    private ItemListener itemListener = new DummyItemListener();

    public class LinkHolder extends RecyclerView.ViewHolder {
        private final TextView lblItem;
        private final TextView lblChannel;

        public LinkHolder(View view) {
            super(view);
            lblItem = (TextView) view.findViewById(R.id.lbl_item);
            lblChannel = (TextView) itemView.findViewById(R.id.lbl_channel);
        }

        public void update(OHLink link){
            lblChannel.setText(link.getChannelUID());
            lblItem.setText(link.getItemName());
        }
    }

    public LinkAdapter() {
    }

    @Override
    public LinkHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new LinkHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_link, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final LinkHolder holder, final int position) {
        final OHLink item = items.get(position);
        holder.update(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItemListener(ItemListener itemListener) {
        if (itemListener == null) {
            this.itemListener = new DummyItemListener();
            return;
        }
        this.itemListener = itemListener;
    }

    public void addItem(OHLink item) {
        items.add(0, item);
        notifyItemInserted(0);
    }

    public void addAll(List<OHLink> items) {
        for (OHLink item : items) {
            this.items.add(0, item);
        }
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        Log.d(TAG, "removeItem: " + position);
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void removeItem(OHLink item) {
        int position = items.indexOf(item);
        items.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * Remove all items from adapter
     */
    public void clear() {
        this.items.clear();
        notifyDataSetChanged();
    }

    interface ItemListener {

        void onItemClickListener(OHLink item);

        boolean onItemLongClickListener(OHLink item);
    }

    public class DummyItemListener implements ItemListener {

        @Override
        public void onItemClickListener(OHLink item) {
        }

        @Override
        public boolean onItemLongClickListener(OHLink item) {
            return false;
        }
    }
}
