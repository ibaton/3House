package treehou.se.habit.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import treehou.se.habit.R

class ImageItemHolder(view: View) : RecyclerView.ViewHolder(view) {
    val lblName: TextView
    val imgIcon: ImageView

    init {
        lblName = view.findViewById(R.id.lbl_title) as TextView
        imgIcon = view.findViewById(R.id.img_item) as ImageView
    }
}
