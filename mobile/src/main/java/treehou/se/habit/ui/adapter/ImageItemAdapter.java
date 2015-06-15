package treehou.se.habit.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import treehou.se.habit.R;
import treehou.se.habit.core.db.controller.ControllerDB;

/**
* Created by ibaton on 2015-03-21.
*/
public class ImageItemAdapter extends RecyclerView.Adapter<ImageItemHolder>{

    private static final String TAG = "ImageItemAdapter";

    private List<ImageItem> items = new ArrayList<>();
    private Context context;

    private OnItemClickListener itemClickListener;

    public ImageItemAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ImageItemHolder onCreateViewHolder(ViewGroup viewGroup, int position) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_menu_image, null);

        Log.d(TAG, "Created ImageItemHolder");
        return new ImageItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ImageItemHolder itemHolder, final int position) {
        final ImageItem item = items.get(position);

        itemHolder.lblName.setText(item.getName());
        itemHolder.imgIcon.setImageResource(item.getImage());
        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener != null){
                    itemClickListener.onItemClicked(item.getId());
                }
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
        for(ImageItem item : items) {
            this.items.add(0, item);
        }
        notifyItemRangeInserted(0, items.size());
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener{
        public void onItemClicked(int id);
    }
}
