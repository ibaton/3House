package treehou.se.habit.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import treehou.se.habit.R
import java.util.*

class MenuAdapter : RecyclerView.Adapter<MenuAdapter.MenuHolder>() {

    private val items = ArrayList<MenuItem>()
    private var listener: OnItemSelectListener = DummyListener()

    inner class MenuHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lblName: TextView
        val imgImage: ImageView

        init {
            lblName = view.findViewById<View>(R.id.lbl_label) as TextView
            imgImage = view.findViewById<View>(R.id.img_menu) as ImageView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): MenuHolder {

        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_menu, parent, false)

        return MenuHolder(itemView)
    }

    override fun onBindViewHolder(serverHolder: MenuHolder, position: Int) {
        val item = items[position]

        serverHolder.imgImage.setImageResource(item.resource)

        serverHolder.lblName.text = item.label
        serverHolder.itemView.setOnClickListener { v -> listener.itemClicked(item.id) }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getItem(position: Int): MenuItem {
        return items[position]
    }

    fun addItem(item: MenuItem) {
        items.add(0, item)
        notifyItemInserted(0)
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeItem(item: MenuItem) {
        val position = items.indexOf(item)
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun clear() {
        this.items.clear()
        notifyDataSetChanged()
    }

    fun addAll(items: List<MenuItem>) {
        for (item in items) {
            this.items.add(0, item)
            notifyItemRangeInserted(0, items.size)
        }
    }

    fun setOnItemSelectListener(listener: OnItemSelectListener) {
        this.listener = listener
    }

    internal inner class DummyListener : OnItemSelectListener {
        override fun itemClicked(id: Int) {}
    }

    interface OnItemSelectListener {
        fun itemClicked(id: Int)
    }
}
