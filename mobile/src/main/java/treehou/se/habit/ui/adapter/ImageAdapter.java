package treehou.se.habit.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import treehou.se.habit.R;

public class ImageAdapter extends ArrayAdapter<ImageItem> {

    public ImageAdapter(Context context, List<ImageItem> objects) {
        super(context, R.layout.item_menu_image, R.id.lbl_title, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ImageItem imageItem = getItem(position);

        View item = inflater.inflate(R.layout.item_menu_image, null);

        ImageView imageView = (ImageView) item.findViewById(R.id.img_item);
        imageView.setImageResource(imageItem.getImage());

        TextView lblTitle = (TextView) item.findViewById(R.id.lbl_title);
        lblTitle.setText(imageItem.getName());

        return item;
    }
}
