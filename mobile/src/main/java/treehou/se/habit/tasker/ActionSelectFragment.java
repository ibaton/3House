package treehou.se.habit.tasker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import treehou.se.habit.R;
import treehou.se.habit.ui.adapter.MenuAdapter;
import treehou.se.habit.ui.adapter.MenuItem;
import treehou.se.habit.ui.adapter.SitemapAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class ActionSelectFragment extends Fragment {

    private static final int MENU_ITEMS = 1;

    public ActionSelectFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tasker_init, container, false);

        MenuAdapter menuAdapter = new MenuAdapter();
        menuAdapter.addItem(new MenuItem(getActivity().getString(R.string.items), MENU_ITEMS, R.drawable.ic_icon_action_item));

        menuAdapter.setOnItemSelectListener(id -> {
            switch (id) {
                case MENU_ITEMS:
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(container.getId(), ItemFragment.newInstance())
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
