package treehou.se.habit.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

import treehou.se.habit.R

class ImageAdapter(context: Context, objects: List<ImageItem>) : ArrayAdapter<ImageItem>(context, R.layout.item_menu_image, R.id.lbl_title, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val imageItem = getItem(position)

        val item = convertView ?: inflater.inflate(R.layout.item_menu_image, parent, false)

        val imageView = item.findViewById<View>(R.id.img_item) as ImageView
        imageView.setImageResource(imageItem!!.image)

        val lblTitle = item.findViewById<View>(R.id.lbl_title) as TextView
        lblTitle.text = imageItem.name

        return item
    }
}
