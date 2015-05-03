package treehou.se.habit.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import treehou.se.habit.R;

/**
* Created by ibaton on 2015-03-05.
*/
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuHolder>{

    private List<MenuItem> items = new ArrayList<>();
    private Context context;
    private OnItemSelectListener listener = new DummyListener();

    public class MenuHolder extends RecyclerView.ViewHolder {
        public final TextView lblName;
        public final ImageView imgImage;

        public MenuHolder(View view) {
            super(view);
            lblName = (TextView) view.findViewById(R.id.lbl_label);
            imgImage = (ImageView) view.findViewById(R.id.img_menu);
        }
    }

    public MenuAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MenuHolder onCreateViewHolder(ViewGroup viewGroup, int position) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_menu, null);

        return new MenuHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuHolder serverHolder, final int position) {
        final MenuItem item = items.get(position);

        serverHolder.imgImage.setImageResource(item.resource);

        serverHolder.lblName.setText(item.label);
        serverHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.itemClicked(item.id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public MenuItem getItem(int position) {
        return items.get(position);
    }

    public void addItem(MenuItem item) {
        items.add(0, item);
        notifyItemInserted(0);
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void removeItem(MenuItem item) {
        int position = items.indexOf(item);
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        this.items.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<MenuItem> items) {
        for(MenuItem item : items) {
            this.items.add(0, item);
            notifyItemRangeInserted(0, items.size());
        }
    }

    public void setOnItemSelectListener(OnItemSelectListener listener){
        this.listener = listener;
    }

    class DummyListener implements OnItemSelectListener{
        @Override
        public void itemClicked(int id) {}
    }

    public static interface OnItemSelectListener{
        public void itemClicked(int id);
    }
}
