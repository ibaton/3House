package treehou.se.habit.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import treehou.se.habit.R
import treehou.se.habit.core.db.model.controller.ControllerDB
import java.util.*

class ControllerAdapter(private val context: Context) : RecyclerView.Adapter<ControllerAdapter.ControllerHolder>() {

    private val items = ArrayList<ControllerDB>()
    private var itemListener: ItemListener = DummyItemListener()

    inner class ControllerHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lblName: TextView

        init {
            lblName = view.findViewById<View>(R.id.lbl_controller) as TextView
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ControllerHolder {

        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.item_controller, viewGroup, false)

        return ControllerHolder(itemView)
    }

    override fun onBindViewHolder(controllerHolder: ControllerHolder, position: Int) {
        val controller = items[position]
        controllerHolder.lblName.text = controller.name
        controllerHolder.itemView.setOnClickListener { v -> itemListener.itemClickListener(controllerHolder) }
        controllerHolder.itemView.setOnLongClickListener { v -> itemListener.itemLongClickListener(controllerHolder) }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface ItemListener {
        fun itemCountUpdated(itemCount: Int)

        fun itemClickListener(controllerHolder: ControllerHolder)

        fun itemLongClickListener(controllerHolder: ControllerHolder): Boolean
    }

    internal inner class DummyItemListener : ItemListener {

        override fun itemCountUpdated(itemCount: Int) {}

        override fun itemClickListener(controllerHolder: ControllerHolder) {}

        override fun itemLongClickListener(controllerHolder: ControllerHolder): Boolean {
            return false
        }
    }

    fun setItemListener(itemListener: ItemListener?) {
        if (itemListener == null) {
            this.itemListener = DummyItemListener()
            return
        }
        this.itemListener = itemListener
    }

    fun getItem(position: Int): ControllerDB {
        return items[position]
    }

    fun removeItem(position: Int) {
        Log.d(TAG, "removeItem: " + position)
        items.removeAt(position)
        notifyItemRemoved(position)
        itemListener.itemCountUpdated(items.size)
    }

    fun addItem(controller: ControllerDB) {
        items.add(0, controller)
        notifyItemInserted(0)
        itemListener.itemCountUpdated(items.size)
    }

    fun addAll(controllers: List<ControllerDB>) {
        for (controller in controllers) {
            items.add(0, controller)
            notifyItemRangeInserted(0, controllers.size)
        }
        itemListener.itemCountUpdated(items.size)
    }

    companion object {

        private val TAG = ControllerAdapter::class.java.simpleName
    }
}
