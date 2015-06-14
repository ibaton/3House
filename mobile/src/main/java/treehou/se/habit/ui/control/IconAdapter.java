package treehou.se.habit.ui.control;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;

import java.util.ArrayList;

import treehou.se.habit.R;
import treehou.se.habit.util.Util;

/**
 * A adapter for selecting an icon.
 */
public class IconAdapter extends RecyclerView.Adapter<IconAdapter.IconHolder> {

    private Context context;
    private ArrayList<IIcon> icons;

    /**
     * Listener that does nothing
     */
    private IconSelectListener dummyIconSelectListener = new IconSelectListener() {
        @Override
        public void iconSelected(IIcon icon) {}
    };
    private IconSelectListener selectListener = dummyIconSelectListener;

    public IconAdapter(Context context) {
        super();

        this.context = context;
        icons = new ArrayList<>();
        icons.addAll(Util.getIcons());
    }

    @Override
    public IconAdapter.IconHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, null);
        return new IconHolder(view);
    }

    @Override
    public void onBindViewHolder(IconAdapter.IconHolder holder, int position) {
        final IIcon icon = icons.get(position);
        holder.setDrawable(new IconicsDrawable(context, icon).color(Color.BLACK).sizeDp(20));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectListener.iconSelected(icon);
            }
        });
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }

    /**
     * Listen for icon selected.
     *
     * @param listener the listener to set. Accepts null.
     */
    public void setIconSelectListener(IconSelectListener listener){
        if(listener == null){
            selectListener = dummyIconSelectListener;
            return;
        }

        selectListener = listener;
    }

    /**
     * Listens for icon select.
     */
    public interface IconSelectListener {
        void iconSelected(IIcon icon);
    }

    /**
     * Find index of item in adapter
     *
     * @param icon
     * @return index of item, -1 if item weren't found
     */
    public int getIndexOf(IIcon icon){
        return icons.indexOf(icon);
    }

    public static class IconHolder extends RecyclerView.ViewHolder {

        private ImageView imgView;

        public IconHolder(View view) {
            super(view);

            imgView = (ImageView) view.findViewById(R.id.img_icon);
        }

        public void setDrawable(Drawable drawable){
            imgView.setImageDrawable(drawable);
        }
    }
}
