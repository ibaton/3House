package treehou.se.habit.ui.util

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon
import treehou.se.habit.R
import treehou.se.habit.ui.BaseFragment
import treehou.se.habit.util.Util
import java.util.*

/**
 * Fragment for picking categories of icons.
 */
class CategoryPickerFragment : BaseFragment() {

    private var lstIcons: RecyclerView? = null
    private var adapter: CategoryAdapter? = null

    private var container: ViewGroup? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_icon_picker, container, false)
        lstIcons = rootView.findViewById<View>(R.id.lst_categories) as RecyclerView
        val gridLayoutManager = GridLayoutManager(activity, 1)
        lstIcons!!.layoutManager = gridLayoutManager
        lstIcons!!.itemAnimator = DefaultItemAnimator()

        // Hookup list of categories
        val categoryList = ArrayList<CategoryPicker>()
        categoryList.add(CategoryPicker(null, getString(R.string.empty), Util.IconCategory.EMPTY))
        categoryList.add(CategoryPicker(CommunityMaterial.Icon.cmd_play, getString(R.string.media), Util.IconCategory.MEDIA))
        categoryList.add(CategoryPicker(CommunityMaterial.Icon.cmd_alarm, getString(R.string.sensor), Util.IconCategory.SENSORS))
        categoryList.add(CategoryPicker(CommunityMaterial.Icon.cmd_power, getString(R.string.command), Util.IconCategory.COMMANDS))
        categoryList.add(CategoryPicker(CommunityMaterial.Icon.cmd_arrow_up, getString(R.string.arrows), Util.IconCategory.ARROWS))
        categoryList.add(CategoryPicker(CommunityMaterial.Icon.cmd_view_module, getString(R.string.all), Util.IconCategory.ALL))
        adapter = CategoryAdapter(categoryList)
        lstIcons!!.adapter = adapter

        this.container = container

        return rootView
    }

    private inner class CategoryPicker(var icon: IIcon?, var category: String?, var id: Util.IconCategory?)

    /**
     * Adapter showing category of icons.
     */
    private inner class CategoryAdapter(val categories: MutableList<CategoryPicker>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        internal inner class CategoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var imgIcon: ImageView = itemView.findViewById<View>(R.id.img_menu) as ImageView
            var lblCategory: TextView = itemView.findViewById<View>(R.id.lbl_label) as TextView
        }

        fun add(category: CategoryPicker) {
            categories.add(category)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val itemView = inflater.inflate(R.layout.item_category, parent, false)

            return CategoryHolder(itemView)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            val item = categories[position]
            val catHolder = holder as CategoryHolder

            catHolder.lblCategory.text = item.category
            if (item.id != Util.IconCategory.EMPTY) {
                val drawable = IconicsDrawable(activity!!, item.icon).color(Color.BLACK).sizeDp(60)
                catHolder.imgIcon.setImageDrawable(drawable)

                holder.itemView.setOnClickListener { v ->
                    activity!!.supportFragmentManager.beginTransaction()
                            .replace(container!!.id, IconPickerFragment.newInstance(item.id!!))
                            .addToBackStack(null)
                            .commit()
                }
            } else {
                catHolder.imgIcon.setImageDrawable(null)
                holder.itemView.setOnClickListener { v ->
                    val intent = Intent()
                    intent.putExtra(IconPickerFragment.RESULT_ICON, "")

                    activity!!.setResult(Activity.RESULT_OK, intent)
                    activity!!.finish()
                }
            }
        }

        override fun getItemCount(): Int {
            return categories.size
        }
    }

    companion object {

        fun newInstance(): CategoryPickerFragment {
            val fragment = CategoryPickerFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }
}
