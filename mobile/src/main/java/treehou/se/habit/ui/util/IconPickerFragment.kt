package treehou.se.habit.ui.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon

import java.util.ArrayList

import treehou.se.habit.R
import treehou.se.habit.ui.BaseFragment
import treehou.se.habit.util.Util

class IconPickerFragment : BaseFragment() {

    private var lstIcons: RecyclerView? = null
    private var adapter: IconAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_icon_picker, container, false)
        lstIcons = rootView.findViewById(R.id.lst_categories)
        lstIcons!!.itemAnimator = DefaultItemAnimator()
        lstIcons!!.layoutManager = GridLayoutManager(activity, 4)

        if (arguments != null) {
            val icons = Util.CAT_ICONS[arguments!!.getSerializable(ARG_CATEGORY)]
            adapter = IconAdapter(icons!!)
        }
        lstIcons!!.adapter = adapter


        return rootView
    }

    private inner class IconAdapter(icons: List<IIcon>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val icons = mutableListOf(*icons.toTypedArray())

        internal inner class IconHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var imgIcon: ImageView

            init {

                imgIcon = itemView.findViewById(R.id.img_menu)
            }
        }

        fun add(icon: IIcon) {
            icons.add(icon)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val itemView = inflater.inflate(R.layout.item_icon, parent, false)

            return IconHolder(itemView)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            val item = icons[position]
            val catHolder = holder as IconHolder

            val drawable = IconicsDrawable(activity!!, item).color(Color.BLACK).sizeDp(40)

            catHolder.imgIcon.setImageDrawable(drawable)
            catHolder.itemView.setOnClickListener { v ->
                val intent = Intent()
                intent.putExtra(RESULT_ICON, item.name)

                activity!!.setResult(Activity.RESULT_OK, intent)
                activity!!.finish()
            }
        }

        override fun getItemCount(): Int {
            return icons.size
        }
    }

    companion object {

        val ARG_CATEGORY = "ARG_CATEGORY"
        val RESULT_ICON = "RESULT_ICON"

        fun newInstance(): IconPickerFragment {
            val fragment = IconPickerFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }

        fun newInstance(category: Util.IconCategory): IconPickerFragment {

            val args = Bundle()
            args.putSerializable(ARG_CATEGORY, category)

            val fragment = IconPickerFragment()
            fragment.arguments = args

            return fragment
        }
    }
}// Required empty public constructor
