package treehou.se.habit.ui.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import treehou.se.habit.R;
import treehou.se.habit.core.db.model.controller.ControllerDB;

public class ImageItemAdapter extends RecyclerView.Adapter<ImageItemHolder>{

    private static final String TAG = "ImageItemAdapter";

    private List<ImageItem> items = new ArrayList<>();
    private Context context;
    private @LayoutRes int layoutItem;

    private OnItemClickListener itemClickListener;

    public ImageItemAdapter(Context context) {
        this(context, R.layout.item_menu_image);
    }

    public ImageItemAdapter(Context context, @LayoutRes int layout) {
        this.context = context;
        this.layoutItem = layout;
    }

    @Override
    public ImageItemHolder onCreateViewHolder(ViewGroup viewGroup, int position) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(layoutItem, viewGroup, false);
        return new ImageItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ImageItemHolder itemHolder, final int position) {
        final ImageItem item = items.get(position);

        itemHolder.lblName.setText(item.getName());
        itemHolder.imgIcon.setImageResource(item.getImage());
        itemHolder.itemView.setOnClickListener(v -> {
            if(itemClickListener != null){
                itemClickListener.onItemClicked(item.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public ImageItem getItem(int position) {
        return items.get(position);
    }

    public void addItem(ImageItem item) {
        items.add(items.size(), item);
        notifyItemInserted(items.size()-1);
    }

    public void removeItem(int position) {
        Log.d(TAG, "removeItem: " + position);
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void removeItem(ControllerDB controller) {
        int position = items.indexOf(controller);
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void addAll(List<ImageItem> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClicked(int id);
    }
}
