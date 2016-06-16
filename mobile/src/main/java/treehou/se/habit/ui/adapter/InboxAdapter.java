package treehou.se.habit.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IntDef;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.connector.models.OHInboxItem;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.ServerDB;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.InboxHolder> {

    private static String TAG = InboxAdapter.class.getSimpleName();

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
        ItemType.item,
        ItemType.item_ignored
    })
    @interface ItemType{
        int item = 1;
        int item_ignored = 2;
    }

    private List<OHInboxItem> items = new ArrayList<>();
    private Context context;

    private ServerDB server;
    private ItemListener itemListener = new DummyItemListener();

    public class InboxHolder extends RecyclerView.ViewHolder {
        public final TextView lblName;
        public LinearLayout louProperties;

        public InboxHolder(View view) {
            super(view);
            lblName = (TextView) view.findViewById(R.id.lbl_server);
            louProperties = (LinearLayout) itemView.findViewById(R.id.lou_properties);
        }
    }

    public class IgnoreInboxHolder extends InboxHolder {

        public IgnoreInboxHolder(View view) {
            super(view);
        }
    }

    public InboxAdapter(Context context, ServerDB server) {
        this.context = context;
        this.server = server;
    }

    @Override
    public int getItemViewType(int position) {
        OHInboxItem item = getItem(position);
        if(item.isIgnored()){
            return ItemType.item_ignored;
        }
        return ItemType.item;
    }

    @Override
    public InboxHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        InboxHolder inboxHolder = null;
        switch (viewType) {
            case ItemType.item:
                View itemView = inflater.inflate(R.layout.item_inbox, null);
                inboxHolder = new InboxHolder(itemView);
                break;
            case ItemType.item_ignored:
                itemView = inflater.inflate(R.layout.item_inbox_ignored, null);
                inboxHolder = new IgnoreInboxHolder(itemView);
                break;
        }
        return inboxHolder;
    }

    @Override
    public void onBindViewHolder(final InboxHolder serverHolder, final int position) {
        final OHInboxItem inboxItem = items.get(position);

        serverHolder.lblName.setText(inboxItem.getLabel());
        serverHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemListener.onItemClickListener(serverHolder);
            }
        });
        serverHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return itemListener.onItemLongClickListener(serverHolder);
            }
        });

        LinearLayout louProperties = serverHolder.louProperties;
        louProperties.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(context);
        for (Map.Entry<String, String> entry : inboxItem.getProperties().entrySet()) {
            View louProperty = inflater.inflate(R.layout.item_property, louProperties, false);
            TextView lblProperty = (TextView) louProperty.findViewById(R.id.lbl_property);
            lblProperty.setText(context.getString(R.string.inbox_property, entry.getKey(), entry.getValue()));

            louProperties.addView(louProperty);
        }

        serverHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.approve_item))
                        .setMessage(context.getString(R.string.approve_this_item))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Openhab.instance(server.toGeneric()).approveInboxItem(inboxItem);
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public OHInboxItem getItem(int position) {
        return items.get(position);
    }

    interface ItemListener {

        void onItemClickListener(InboxHolder serverHolder);

        boolean onItemLongClickListener(InboxHolder serverHolder);

        void itemCountUpdated(int itemCount);
    }

    public class DummyItemListener implements ItemListener {

        @Override
        public void onItemClickListener(InboxHolder serverHolder) {
        }

        @Override
        public boolean onItemLongClickListener(InboxHolder serverHolder) {
            return false;
        }

        @Override
        public void itemCountUpdated(int itemCount) {
        }
    }

    public void setItemListener(ItemListener itemListener) {
        if (itemListener == null) {
            this.itemListener = new DummyItemListener();
            return;
        }
        this.itemListener = itemListener;
    }

    public void addItem(OHInboxItem item) {
        items.add(0, item);
        notifyItemInserted(0);
        itemListener.itemCountUpdated(items.size());
    }

    public void addAll(List<OHInboxItem> items) {
        for (OHInboxItem item : items) {
            this.items.add(0, item);
        }
        notifyDataSetChanged();
        itemListener.itemCountUpdated(items.size());
    }

    public void removeItem(int position) {
        Log.d(TAG, "removeItem: " + position);
        items.remove(position);
        notifyItemRemoved(position);
        itemListener.itemCountUpdated(items.size());
    }

    public void removeItem(OHInboxItem item) {
        int position = items.indexOf(item);
        items.remove(position);
        notifyItemRemoved(position);
        itemListener.itemCountUpdated(items.size());
    }

    /**
     * Remove all items from adapter
     */
    public void clear() {
        this.items.clear();
        notifyDataSetChanged();
        itemListener.itemCountUpdated(items.size());
    }
}
