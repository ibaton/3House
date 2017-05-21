package treehou.se.habit.ui.menu;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import treehou.se.habit.R;

class DrawerItemHolder extends RecyclerView.ViewHolder {

    private ImageView imgIcon;
    private TextView lblName;

    public DrawerItemHolder(View itemView) {
        super(itemView);
        imgIcon = (ImageView) itemView.findViewById(R.id.img_icon);
        lblName = (TextView) itemView.findViewById(R.id.lbl_name);
    }

    public void update(DrawerItem entry){
        lblName.setText(entry.getName());
        if(entry.getResource() != 0) {
            imgIcon.setImageResource(entry.getResource());
            imgIcon.setColorFilter(lblName.getCurrentTextColor());
        }
    }
}
