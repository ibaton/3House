package treehou.se.habit.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import treehou.se.habit.R;

/**
* Created by ibaton on 2015-03-21.
*/
public class ImageItemHolder extends RecyclerView.ViewHolder {
    public final TextView lblName;
    public final ImageView imgIcon;

    public ImageItemHolder(View view) {
        super(view);
        lblName = (TextView) view.findViewById(R.id.lbl_title);
        imgIcon = (ImageView) view.findViewById(R.id.img_item);
    }
}
