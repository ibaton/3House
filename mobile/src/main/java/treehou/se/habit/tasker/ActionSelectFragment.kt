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
import treehou.se.habit.ui.adapter.MenuAdapter
import treehou.se.habit.ui.adapter.MenuItem

/**
 * A placeholder fragment containing a simple view.
 */
class ActionSelectFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_tasker_init, container, false)

        val menuAdapter = MenuAdapter()
        menuAdapter.addItem(MenuItem(activity!!.getString(R.string.items), MENU_ITEMS, R.drawable.ic_icon_action_item))

        val listener = object : MenuAdapter.OnItemSelectListener {
            override fun itemClicked(id: Int) {
                when (id) {
                    MENU_ITEMS -> activity!!.supportFragmentManager
                            .beginTransaction()
                            .replace(container!!.id, ItemFragment.newInstance())
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

        private val MENU_ITEMS = 1
    }
}
