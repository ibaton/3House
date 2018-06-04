package treehou.se.habit.ui.menu


import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import treehou.se.habit.R

internal class DrawerItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val imgIcon: ImageView
    private val lblName: TextView

    init {
        imgIcon = itemView.findViewById<View>(R.id.imgIcon) as ImageView
        lblName = itemView.findViewById<View>(R.id.lbl_name) as TextView
    }

    fun update(entry: DrawerItem) {
        lblName.text = entry.name
        if (entry.resource != 0) {
            imgIcon.setImageResource(entry.resource)
            imgIcon.setColorFilter(lblName.currentTextColor)
        }
    }
}
