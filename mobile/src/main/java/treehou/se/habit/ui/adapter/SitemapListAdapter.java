package treehou.se.habit.ui.adapter;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.ServerDB;

public class SitemapListAdapter extends RecyclerView.Adapter<SitemapListAdapter.SitemapBaseHolder> {

    @IntDef({STATE_SUCCESS, STATE_LOADING, STATE_ERROR})
    public @interface ServerState {}
    public static final int STATE_SUCCESS = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_ERROR = 2;

    protected LayoutInflater inflater;
    protected Context context;
    private Map<ServerDB, SitemapItem> items = new HashMap<>();
    private SitemapSelectedListener sitemapSelectedListener = new DummySitemapSelectListener();

    private static class SitemapItem{

        public ServerDB server;
        public @ServerState int state = STATE_LOADING;
        public List<OHSitemap> sitemaps = new ArrayList<>();

        public SitemapItem(ServerDB server) {
            this.server = server;
        }

        public void addItem(OHSitemap sitemap){
            sitemaps.add(sitemap);
            state = STATE_SUCCESS;
        }
    }

    public class SitemapBaseHolder extends RecyclerView.ViewHolder {

        public TextView lblServer;

        public SitemapBaseHolder(View itemView) {
            super(itemView);
            lblServer = (TextView) itemView.findViewById(R.id.lbl_server);
        }
    }

    public class SitemapHolder extends SitemapBaseHolder {
        public TextView lblName;

        public SitemapHolder(View view) {
            super(view);

            lblName = (TextView) itemView.findViewById(R.id.lbl_sitemap);
        }
    }

    public interface SitemapSelectedListener {
        void onSelected(ServerDB server, OHSitemap sitemap);
        void onErrorSelected(ServerDB server);
    }

    class DummySitemapSelectListener implements SitemapSelectedListener {
        @Override
        public void onSelected(ServerDB server, OHSitemap sitemap) {}

        @Override
        public void onErrorSelected(ServerDB server) {}
    }

    public class SitemapErrorHolder extends SitemapBaseHolder {
        public SitemapErrorHolder(View view) {
            super(view);
        }
    }

    public class SitemapLoadHolder extends SitemapBaseHolder {
        public SitemapLoadHolder(View view) {
            super(view);
        }
    }

    public class GetResult {

        public SitemapItem item;
        public OHSitemap sitemap;

        public GetResult(SitemapItem item, OHSitemap sitemap) {
            this.sitemap = sitemap;
            this.item = item;
        }
    }

    public SitemapListAdapter(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public SitemapBaseHolder onCreateViewHolder(ViewGroup viewGroup, int type) {

        LayoutInflater inflater = LayoutInflater.from(context);
        if (STATE_SUCCESS == type) {
            View itemView = inflater.inflate(R.layout.item_sitemap, null);
            return new SitemapHolder(itemView);
        } else if (STATE_LOADING == type) {
            View itemView = inflater.inflate(R.layout.item_sitemap_load, null);
            return new SitemapLoadHolder(itemView);
        } else {
            View serverLoadFail = inflater.inflate(R.layout.item_sitemap_failed, null);
            return new SitemapErrorHolder(serverLoadFail);
        }
    }

    @Override
    public void onBindViewHolder(final SitemapBaseHolder sitemapHolder, int position) {

        int type = getItemViewType(position);
        final GetResult item = getItem(position);

        final OHSitemap sitemap = item.sitemap;
        final ServerDB server = item.item.server;
        if (STATE_SUCCESS == type) {
            SitemapHolder holder = (SitemapHolder) sitemapHolder;

            holder.lblName.setText(sitemap.getDisplayName());
            holder.lblServer.setText(server.getDisplayName());

            sitemapHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sitemapSelectedListener.onSelected(server, sitemap);
                }
            });
        } else if (STATE_LOADING == type) {
            SitemapLoadHolder holder = (SitemapLoadHolder) sitemapHolder;
            holder.lblServer.setText(server.getDisplayName());
        } else if (STATE_ERROR == type) {
            SitemapErrorHolder holder = (SitemapErrorHolder) sitemapHolder;
            holder.lblServer.setText(server.getDisplayName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sitemapSelectedListener.onErrorSelected(server);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        int count = 0;
        for (SitemapItem item : items.values()) {
            if (STATE_SUCCESS == item.state) {
                if (position >= count && position < (count + item.sitemaps.size())) {
                    return STATE_SUCCESS;
                }
                count += item.sitemaps.size();
            } else if (STATE_ERROR == item.state) {
                if (count == position) {
                    return STATE_ERROR;
                }
                count++;
            } else if (STATE_LOADING == item.state) {
                if (count == position) {
                    return STATE_LOADING;
                }
                count++;
            }
        }

        return STATE_LOADING;
    }

    @Override
    public int getItemCount() {

        int count = 0;
        for (SitemapItem item : items.values()) {
            if (item.state == STATE_SUCCESS) {
                count += item.sitemaps.size();
            } else {
                count++;
            }
        }

        return count;
    }

    /**
     * Returns item at a certain position
     *
     * @param position item to grab item for
     * @return
     */
    public GetResult getItem(int position) {
        GetResult result = null;
        int count = 0;
        for (SitemapItem item : items.values()) {
            if (STATE_SUCCESS == item.state) {
                for (OHSitemap sitemap : item.sitemaps) {
                    if (count == position) {
                        result = new GetResult(item, sitemap);
                        return result;
                    }
                    count++;
                }
            } else {
                if (count == position) {
                    result = new GetResult(item, null);
                    break;
                }
                count++;
            }
        }

        return result;
    }

    public void addAll(ServerDB server, List<OHSitemap> sitemapIds) {
        for (OHSitemap sitemap : sitemapIds) {
            add(server, sitemap);
        }
    }

    public void add(ServerDB server, OHSitemap sitemap) {
        SitemapItem item = items.get(server);
        if (item == null) {
            item = new SitemapItem(server);
            items.put(item.server, item);
        }
        item.addItem(sitemap);

        notifyDataSetChanged();
    }

    /**
     * Remove all sitemap entries from adapter.
     */
    public void clear() {
        items.clear();
        notifyItemRangeRemoved(0, items.size() - 1);
    }

    public void remove(OHSitemap sitemap) {
        int pos = findPosition(sitemap);
        remove(sitemap, pos);
    }

    public void remove(OHSitemap sitemap, int position) {
        SitemapItem item = null; // TODO items.get(serverDB.getId());
        if (sitemap == null) {
            return;
        }

        item.sitemaps.remove(sitemap);
        notifyItemRemoved(position);
    }

    /**
     * Add listener for when sitemap item is clicked
     *
     * @param sitemapSelectedListener listens for click on sitemap.
     */
    public void setSitemapSelectedListener(SitemapSelectedListener sitemapSelectedListener) {
        if (sitemapSelectedListener == null) {
            this.sitemapSelectedListener = new DummySitemapSelectListener();
            return;
        }
        this.sitemapSelectedListener = sitemapSelectedListener;

    }

    private int findPosition(final OHSitemap sitemap) {
        int count = 0;
        for (SitemapItem item : items.values()) {
            if (STATE_SUCCESS == item.state) {
                for (OHSitemap sitemapIter : item.sitemaps) {
                    if (sitemap == sitemapIter) {
                        return count;
                    }
                    count++;
                }
            } else {
                count++;
            }
        }
        return -1;
    }

    /**
     * Get a sitemap item. Creates a new server item if no item exists.
     *
     * @param server server to get sitemap item for.
     * @return
     */
    private SitemapItem getItem(ServerDB server) {
        SitemapItem item = items.get(server);
        if (item == null) {
            item = new SitemapItem(server);
            items.put(server, item);
        }
        return item;
    }

    public void setServerState(ServerDB server, @ServerState int state) {
        SitemapItem item = getItem(server);
        item.state = state;

        notifyDataSetChanged();
    }

    public boolean contains(OHSitemap sitemap) {
        return items.containsKey(sitemap.getServer()) && items.get(sitemap.getServer()).sitemaps.contains(sitemap);
    }
}
