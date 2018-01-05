package treehou.se.habit.ui.adapter


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import java.util.ArrayList

import se.treehou.ng.ohcommunicator.connector.models.OHBinding
import treehou.se.habit.R
import treehou.se.habit.ui.bindings.BindingsFragment

class BindingAdapter : RecyclerView.Adapter<BindingAdapter.BindingHolder>() {

    private val bindings = ArrayList<OHBinding>()
    private var itemClickListener: ItemClickListener? = null

    inner class BindingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val lblName: TextView
        val lblAuthor: TextView
        val lblDescription: TextView

        init {

            lblName = itemView.findViewById<View>(R.id.lbl_name) as TextView
            lblAuthor = itemView.findViewById<View>(R.id.lbl_author) as TextView
            lblDescription = itemView.findViewById<View>(R.id.lbl_description) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder {

        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_binding, parent, false)

        return BindingHolder(itemView)
    }

    override fun onBindViewHolder(holder: BindingHolder, position: Int) {

        val binding = bindings[position]
        holder.lblName.text = binding.name
        holder.lblAuthor.text = binding.author
        holder.lblDescription.text = binding.description

        holder.itemView.setOnClickListener { v -> itemClickListener!!.onClick(binding) }
    }

    /**
     * Set listener listening for selections of bindings.
     * @param itemClickListener
     */
    fun setItemClickListener(itemClickListener: ItemClickListener?) {
        var itemClickListener = itemClickListener
        if (itemClickListener == null) {
            itemClickListener = DummyItemListener()
        }
        this.itemClickListener = itemClickListener
    }

    override fun getItemCount(): Int {
        return bindings.size
    }

    fun addBinding(binding: OHBinding) {
        bindings.add(binding)
        notifyItemInserted(bindings.size - 1)
    }

    fun setBindings(newBindings: List<OHBinding>) {
        bindings.clear()
        bindings.addAll(newBindings)
        notifyDataSetChanged()
    }

    interface ItemClickListener {
        fun onClick(binding: OHBinding)
    }

    private inner class DummyItemListener : ItemClickListener {
        override fun onClick(binding: OHBinding) {}
    }
}
