package treehou.se.habit.tasker

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import treehou.se.habit.R
import treehou.se.habit.tasker.items.CommandActionFragment
import treehou.se.habit.tasker.items.IncDecActionFragment
import treehou.se.habit.tasker.items.SwitchActionFragment
import treehou.se.habit.ui.adapter.MenuAdapter
import treehou.se.habit.ui.adapter.MenuItem


class ItemFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_item, container, false)

        val menuAdapter = MenuAdapter()
        menuAdapter.addItem(MenuItem(getString(R.string.command), MENU_ITEM_COMMAND, R.drawable.ic_icon_sitemap))
        menuAdapter.addItem(MenuItem(getString(R.string.label_switch), MENU_ITEM_SWITCH, R.drawable.ic_icon_sitemap))
        menuAdapter.addItem(MenuItem(getString(R.string.inc_dec), MENU_ITEM_INC_DEC, R.drawable.ic_icon_sitemap))

        val listener = object: MenuAdapter.OnItemSelectListener{
            override fun itemClicked(id: Int) {
                when (id) {
                    MENU_ITEM_SWITCH -> activity!!.supportFragmentManager
                            .beginTransaction()
                            .replace(container!!.id, SwitchActionFragment.newInstance())
                            .addToBackStack(null)
                            .commit()
                    MENU_ITEM_COMMAND -> activity!!.supportFragmentManager
                            .beginTransaction()
                            .replace(container!!.id, CommandActionFragment.newInstance())
                            .addToBackStack(null)
                            .commit()
                    MENU_ITEM_INC_DEC -> activity!!.supportFragmentManager
                            .beginTransaction()
                            .replace(container!!.id, IncDecActionFragment.newInstance())
                            .addToBackStack(null)
                            .commit()
                }
            }
        }

        menuAdapter.setOnItemSelectListener(listener)

        val lstItems = rootView.findViewById<View>(R.id.list) as RecyclerView
        val gridLayoutManager = GridLayoutManager(activity, 1)
        lstItems.layoutManager = gridLayoutManager
        lstItems.itemAnimator = DefaultItemAnimator()
        lstItems.adapter = menuAdapter

        return rootView
    }

    companion object {

        val MENU_ITEM_SWITCH = 0
        val MENU_ITEM_COMMAND = 1
        val MENU_ITEM_INC_DEC = 2

        fun newInstance(): ItemFragment {
            val fragment = ItemFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
