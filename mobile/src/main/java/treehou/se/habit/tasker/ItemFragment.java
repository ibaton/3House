package treehou.se.habit.tasker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import treehou.se.habit.R;
import treehou.se.habit.tasker.items.CommandActionFragment;
import treehou.se.habit.tasker.items.IncDecActionFragment;
import treehou.se.habit.tasker.items.SwitchActionFragment;
import treehou.se.habit.ui.adapter.MenuAdapter;
import treehou.se.habit.ui.adapter.MenuItem;


public class ItemFragment extends Fragment {

    public static final int MENU_ITEM_SWITCH    = 0;
    public static final int MENU_ITEM_COMMAND   = 1;
    public static final int MENU_ITEM_INC_DEC   = 2;

    public static ItemFragment newInstance() {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_item, container, false);

        MenuAdapter menuAdapter = new MenuAdapter(getActivity());
        menuAdapter.addItem(new MenuItem(getString(R.string.command), MENU_ITEM_COMMAND, R.drawable.ic_icon_sitemap));
        menuAdapter.addItem(new MenuItem(getString(R.string.label_switch), MENU_ITEM_SWITCH, R.drawable.ic_icon_sitemap));
        menuAdapter.addItem(new MenuItem(getString(R.string.inc_dec), MENU_ITEM_INC_DEC, R.drawable.ic_icon_sitemap));

        menuAdapter.setOnItemSelectListener(id -> {
            switch (id){
                case MENU_ITEM_SWITCH :
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(container.getId(), SwitchActionFragment.newInstance())
                            .addToBackStack(null)
                            .commit();
                    break;
                case MENU_ITEM_COMMAND :
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(container.getId(), CommandActionFragment.newInstance())
                            .addToBackStack(null)
                            .commit();
                    break;
                case MENU_ITEM_INC_DEC :
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(container.getId(), IncDecActionFragment.newInstance())
                            .addToBackStack(null)
                            .commit();
                    break;
            }
        });

        RecyclerView lstItems = (RecyclerView) rootView.findViewById(R.id.list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        lstItems.setLayoutManager(gridLayoutManager);
        lstItems.setItemAnimator(new DefaultItemAnimator());
        lstItems.setAdapter(menuAdapter);

        return rootView;
    }
}
