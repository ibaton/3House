package treehou.se.habit.ui.adapter

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import treehou.se.habit.R
import treehou.se.habit.core.db.model.controller.ControllerDB

class ImageItemAdapter @JvmOverloads constructor(@param:LayoutRes @field:LayoutRes private val layoutItem: Int = R.layout.item_menu_image) : RecyclerView.Adapter<ImageItemHolder>() {

    private val items = mutableListOf<ImageItem>()

    private var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ImageItemHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val itemView = inflater.inflate(layoutItem, viewGroup, false)
        return ImageItemHolder(itemView)
    }

    override fun onBindViewHolder(itemHolder: ImageItemHolder, position: Int) {
        val item = items[position]

        itemHolder.lblName.text = item.name
        itemHolder.imgIcon.setImageResource(item.image)
        itemHolder.itemView.setOnClickListener { v ->
            if (itemClickListener != null) {
                itemClickListener!!.onItemClicked(item.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getItem(position: Int): ImageItem {
        return items[position]
    }

    fun addItem(item: ImageItem) {
        items.add(items.size, item)
        notifyItemInserted(items.size - 1)
    }

    fun removeItem(position: Int) {
        Log.d(TAG, "removeItem: " + position)
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeItem(item: ImageItem) {
        val position = items.indexOf(item)
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addAll(items: List<ImageItem>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun setItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    interface OnItemClickListener {
        fun onItemClicked(id: Int)
    }

    companion object {

        private val TAG = "ImageItemAdapter"
    }
}
