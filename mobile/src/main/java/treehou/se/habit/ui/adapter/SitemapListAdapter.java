package treehou.se.habit.ui.adapter;

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

import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import treehou.se.habit.R;

public class SitemapListAdapter extends RecyclerView.Adapter<SitemapListAdapter.SitemapBaseHolder> {

    @IntDef(
        {ServerState.STATE_SUCCESS,
        ServerState.STATE_LOADING,
        ServerState.STATE_ERROR,
        ServerState.STATE_CERTIFICATE_ERROR}
    )
    public @interface ServerState {
        int STATE_SUCCESS = 0;
        int STATE_LOADING = 1;
        int STATE_ERROR = 2;
        int STATE_CERTIFICATE_ERROR = 3;
    }

    private Map<OHServer, SitemapItem> items = new HashMap<>();
    private SitemapSelectedListener sitemapSelectedListener = new DummySitemapSelectListener();

    private static class SitemapItem{

        public OHServer server;
        public @ServerState int state = ServerState.STATE_LOADING;
        public List<OHSitemap> sitemaps = new ArrayList<>();

        public SitemapItem(OHServer server) {
            this.server = server;
        }

        public void addItem(OHSitemap sitemap){
            sitemaps.add(sitemap);
            state = ServerState.STATE_SUCCESS;
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
        void onSelected(OHServer server, OHSitemap sitemap);
        void onErrorSelected(OHServer server);
        void onCertificateErrorSelected(OHServer server);
    }

    class DummySitemapSelectListener implements SitemapSelectedListener {
        @Override
        public void onSelected(OHServer server, OHSitemap sitemap) {}

        @Override
        public void onErrorSelected(OHServer server) {}

        @Override
        public void onCertificateErrorSelected(OHServer server) {

        }
    }

    public class SitemapErrorHolder extends SitemapBaseHolder {
        public SitemapErrorHolder(View view) {
            super(view);
        }
    }

    public class SitemapCertificateErrorHolder extends SitemapBaseHolder {
        public SitemapCertificateErrorHolder(View view) {
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

    public SitemapListAdapter() {
    }

    @Override
    public SitemapBaseHolder onCreateViewHolder(ViewGroup parent, int type) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (ServerState.STATE_SUCCESS == type) {
            View itemView = inflater.inflate(R.layout.item_sitemap, parent, false);
            return new SitemapHolder(itemView);
        } else if (ServerState.STATE_LOADING == type) {
            View itemView = inflater.inflate(R.layout.item_sitemap_load, parent, false);
            return new SitemapLoadHolder(itemView);
        } else if (ServerState.STATE_CERTIFICATE_ERROR == type) {
            View itemView = inflater.inflate(R.layout.item_sitemap_certificate_failed, parent, false);
            return new SitemapCertificateErrorHolder(itemView);
        } else {
            View serverLoadFail = inflater.inflate(R.layout.item_sitemap_failed, parent, false);
            return new SitemapErrorHolder(serverLoadFail);
        }
    }

    @Override
    public void onBindViewHolder(final SitemapBaseHolder sitemapHolder, int position) {

        int type = getItemViewType(position);
        final GetResult item = getItem(position);

        final OHSitemap sitemap = item.sitemap;
        final OHServer server = item.item.server;
        if (ServerState.STATE_SUCCESS == type) {
            SitemapHolder holder = (SitemapHolder) sitemapHolder;

            holder.lblName.setText(sitemap.getDisplayName());
            holder.lblServer.setText(server.getDisplayName());

            sitemapHolder.itemView.setOnClickListener(v -> sitemapSelectedListener.onSelected(server, sitemap));
        } else if (ServerState.STATE_LOADING == type) {
            SitemapLoadHolder holder = (SitemapLoadHolder) sitemapHolder;
            holder.lblServer.setText(server.getDisplayName());
        } else if (ServerState.STATE_ERROR == type) {
            SitemapErrorHolder holder = (SitemapErrorHolder) sitemapHolder;
            holder.lblServer.setText(server.getDisplayName());
            holder.itemView.setOnClickListener(v -> sitemapSelectedListener.onErrorSelected(server));
        } else if (ServerState.STATE_CERTIFICATE_ERROR == type) {
            SitemapCertificateErrorHolder holder = (SitemapCertificateErrorHolder) sitemapHolder;
            holder.lblServer.setText(server.getDisplayName());
            holder.itemView.setOnClickListener(v -> sitemapSelectedListener.onCertificateErrorSelected(server));
        }
    }

    @Override
    public int getItemViewType(int position) {
        int count = 0;
        for (SitemapItem item : items.values()) {
            if (ServerState.STATE_SUCCESS == item.state) {
                if (position >= count && position < (count + item.sitemaps.size())) {
                    return ServerState.STATE_SUCCESS;
                }
                count += item.sitemaps.size();
            } else if (ServerState.STATE_ERROR == item.state) {
                if (count == position) {
                    return ServerState.STATE_ERROR;
                }
                count++;
            } else if (ServerState.STATE_CERTIFICATE_ERROR == item.state) {
                if (count == position) {
                    return ServerState.STATE_CERTIFICATE_ERROR;
                }
                count++;
            } else if (ServerState.STATE_LOADING == item.state) {
                if (count == position) {
                    return ServerState.STATE_LOADING;
                }
                count++;
            }
        }

        return ServerState.STATE_LOADING;
    }

    @Override
    public int getItemCount() {

        int count = 0;
        for (SitemapItem item : items.values()) {
            if (item.state == ServerState.STATE_SUCCESS) {
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
            if (ServerState.STATE_SUCCESS == item.state) {
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

    public void addAll(OHServer server, List<OHSitemap> sitemapIds) {
        for (OHSitemap sitemap : sitemapIds) {
            add(server, sitemap);
        }
    }

    public void add(OHServer server, OHSitemap sitemap) {
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
            if (ServerState.STATE_SUCCESS == item.state) {
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
    private SitemapItem getItem(OHServer server) {
        SitemapItem item = items.get(server);
        if (item == null) {
            item = new SitemapItem(server);
            items.put(server, item);
        }
        return item;
    }

    public void setServerState(OHServer server, @ServerState int state) {
        SitemapItem item = getItem(server);
        item.state = state;

        notifyDataSetChanged();
    }

    public boolean contains(OHSitemap sitemap) {
        return items.containsKey(sitemap.getServer()) && items.get(sitemap.getServer()).sitemaps.contains(sitemap);
    }
}
