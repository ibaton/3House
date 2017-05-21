package treehou.se.habit.ui.menu;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import treehou.se.habit.R;

class DrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_SITEMAP = 2;

    private List<DrawerItem> items = new ArrayList<>();
    private List<OHSitemap> sitemaps = new ArrayList<>();
    private OnItemClickListener itemClickListener;
    private OnSitemapClickListener sitemapItemClickListener;

    interface  OnItemClickListener{
        void onClickItem(DrawerItem item);
    }

    interface OnSitemapClickListener {
        void onClickItem(OHSitemap item);
    }

    static class SitemapItemHolder extends RecyclerView.ViewHolder {

        private TextView lblName;

        public SitemapItemHolder(View itemView) {
            super(itemView);
            lblName = (TextView) itemView.findViewById(R.id.lbl_sitemap);
        }

        public void update(OHSitemap entry){
            lblName.setText(entry.getDisplayName());
        }
    }

    public DrawerAdapter() {
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setSitemapsClickListener(OnSitemapClickListener itemClickListener) {
        sitemapItemClickListener = itemClickListener;
    }

    /**
     * Add menu items to add.
     * @param drawerItems the items to add.
     */
    public void add(List<DrawerItem> drawerItems){
        items.addAll(drawerItems);
        notifyItemRangeInserted(0, drawerItems.size());
    }

    /**
     * Add sitemaps that should be shown in menu.
     *
     * @param sitemaps the sitemaps that should be shown in menu.
     */
    public void addSitemaps(List<OHSitemap> sitemaps){
        this.sitemaps.addAll(sitemaps);
        notifyDataSetChanged();
    }

    /**
     * Remove all sitemaps added to menu.
     */
    public void clearSitemaps(){
        sitemaps.clear();
        notifyDataSetChanged();
    }

    /**
     * Get sitemap from position.
     * @param position the menu position to get sitemap for.
     * @return sitemap at position.
     */
    private OHSitemap getSitemap(int position){
        return sitemaps.get(position-(findPosition(NavigationDrawerFragment.NavigationItems.ITEM_SITEMAPS)+1));
    }

    /**
     * Get sitemap from position.
     * @param position the menu position to get sitemap for.
     * @return sitemap at position.
     */
    private DrawerItem getMenuItem(int position){
        int mainItemPosition = 0;
        for(int i=0; i<getItemCount(); i++){
            int itemViewType = getItemViewType(i);

            if(VIEW_TYPE_ITEM == itemViewType){
                DrawerItem item = items.get(mainItemPosition);
                if(position == i) {
                    return item;
                }
                mainItemPosition++;
            }
        }
        return null;
    }

    /**
     * Get menu item position.
     *
     * @param navigationItem the navigation item to search for.
     * @return navigation item position.
     */
    private int findPosition(@NavigationDrawerFragment.NavigationItems int navigationItem){
        int mainItemPosition = 0;
        for(int i=0; i<getItemCount(); i++){
            int itemViewType = getItemViewType(i);

            if(VIEW_TYPE_ITEM == itemViewType){
                DrawerItem item = items.get(mainItemPosition);
                if(item.getValue() == navigationItem){
                    return i;
                }
                mainItemPosition++;
            }
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        RecyclerView.ViewHolder drawerItemHolder;
        if(VIEW_TYPE_SITEMAP == viewType){
            View itemView = inflater.inflate(R.layout.item_sitemap_small, parent, false);
            drawerItemHolder = new SitemapItemHolder(itemView);
        }
        else {
            View itemView = inflater.inflate(R.layout.item_drawer, parent, false);
            drawerItemHolder = new DrawerItemHolder(itemView);
        }

        return drawerItemHolder;
    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemType = getItemViewType(position);

        if(VIEW_TYPE_SITEMAP == itemType){
            OHSitemap sitemap = getSitemap(position);
            SitemapItemHolder sitemapItemHolder = (SitemapItemHolder) holder;
            sitemapItemHolder.update(sitemap);
            sitemapItemHolder.itemView.setOnClickListener(view -> {
                if (sitemapItemClickListener != null) {
                    sitemapItemClickListener.onClickItem(sitemap);
                }
            });
        }
        else {
            DrawerItem drawerItem = getMenuItem(position);
            DrawerItemHolder drawerItemHolder = (DrawerItemHolder) holder;
            drawerItemHolder.update(drawerItem);
            drawerItemHolder.itemView.setOnClickListener(view -> {
                if (itemClickListener != null) {
                    itemClickListener.onClickItem(drawerItem);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {

        int menuPosition = 0;
        for(int i=0; i<=position; i++){
            DrawerItem menuItem = items.get(menuPosition);
            if(i == position){
                return VIEW_TYPE_ITEM;
            }

            if(menuItem.getValue() == NavigationDrawerFragment.NavigationItems.ITEM_SITEMAPS){
                for(int sitemapPosition=0; sitemapPosition<sitemaps.size(); sitemapPosition++){
                    i++;
                    if(i == position){
                        return VIEW_TYPE_SITEMAP;
                    }
                }
            }
            menuPosition++;
        }

        return -1;
    }

    @Override
    public int getItemCount() {
        return items.size() + sitemaps.size();
    }
}
