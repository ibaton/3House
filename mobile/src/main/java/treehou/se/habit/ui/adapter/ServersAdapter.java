package treehou.se.habit.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.ServerDB;

public class ServersAdapter extends RecyclerView.Adapter<ServersAdapter.ServerHolder> {

    private RealmResults<ServerDB> realmResults;
    private Context context;
    private ItemListener itemListener = new DummyItemListener();

    public class ServerHolder extends RecyclerView.ViewHolder {
        public final TextView lblName;

        public ServerHolder(View view) {
            super(view);
            lblName = (TextView) view.findViewById(R.id.lbl_server);
        }
    }

    public ServersAdapter(Context context, final RealmResults<ServerDB> realmResults) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.context = context;
        this.realmResults = realmResults;
    }

    @Override
    public ServerHolder onCreateViewHolder(ViewGroup viewGroup, int position) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_server, null);

        return new ServerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ServerHolder serverHolder, final int position) {
        ServerDB server = realmResults.get(position);

        serverHolder.lblName.setText(server.getDisplayName());
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
    }

    @Override
    public int getItemCount() {
        return realmResults.size();
    }

    public ServerDB getItem(int position) {
        return realmResults.get(position);
    }

    public interface ItemListener {

        void onItemClickListener(ServerHolder serverHolder);

        boolean onItemLongClickListener(ServerHolder serverHolder);
    }

    public class DummyItemListener implements ItemListener {

        @Override
        public void onItemClickListener(ServerHolder serverHolder) {
        }

        @Override
        public boolean onItemLongClickListener(ServerHolder serverHolder) {
            return false;
        }
    }

    /**
     * Add adapter change listener
     * @param itemListener
     */
    public void setItemListener(ItemListener itemListener) {
        if (itemListener == null) {
            this.itemListener = new DummyItemListener();
            return;
        }
        this.itemListener = itemListener;
    }
}
