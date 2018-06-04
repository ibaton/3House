package treehou.se.habit.ui.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon

import java.util.ArrayList

import treehou.se.habit.R
import treehou.se.habit.util.Util

/**
 * A adapter for selecting an icon.
 */
class IconAdapter(private val context: Context) : RecyclerView.Adapter<IconAdapter.IconHolder>() {
    private val icons: ArrayList<IIcon>

    /**
     * Listener that does nothing
     */
    private val dummyIconSelectListener = object : IconSelectListener{
        override fun iconSelected(icon: IIcon) {}
    }

    private var selectListener: IconSelectListener = dummyIconSelectListener

    init {
        icons = ArrayList()
        icons.addAll(Util.icons)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconAdapter.IconHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        return IconHolder(view)
    }

    override fun onBindViewHolder(holder: IconAdapter.IconHolder, position: Int) {
        val icon = icons[position]
        holder.setDrawable(IconicsDrawable(context, icon).color(Color.BLACK).sizeDp(20))
        holder.itemView.setOnClickListener { v -> selectListener.iconSelected(icon) }
    }

    override fun getItemCount(): Int {
        return icons.size
    }

    /**
     * Listen for icon selected.
     *
     * @param listener the listener to set. Accepts null.
     */
    fun setIconSelectListener(listener: IconSelectListener?) {
        if (listener == null) {
            selectListener = dummyIconSelectListener
            return
        }

        selectListener = listener
    }

    /**
     * Listens for icon select.
     */
    interface IconSelectListener {
        fun iconSelected(icon: IIcon)
    }

    /**
     * Find index of item in adapter
     *
     * @param icon
     * @return index of item, -1 if item weren't found
     */
    fun getIndexOf(icon: IIcon): Int {
        return icons.indexOf(icon)
    }

    class IconHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imgView: ImageView

        init {

            imgView = view.findViewById<View>(R.id.imgIcon) as ImageView
        }

        fun setDrawable(drawable: Drawable) {
            imgView.setImageDrawable(drawable)
        }
    }
}
