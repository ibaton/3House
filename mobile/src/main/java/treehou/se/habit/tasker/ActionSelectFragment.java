package treehou.se.habit.tasker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import treehou.se.habit.R;
import treehou.se.habit.core.Sitemap;
import treehou.se.habit.core.db.SitemapDB;
import treehou.se.habit.tasker.boundle.OpenSitemapBoundleManager;
import treehou.se.habit.ui.SitemapSelectorFragment;
import treehou.se.habit.ui.adapter.MenuAdapter;
import treehou.se.habit.ui.adapter.MenuItem;

/**
 * A placeholder fragment containing a simple view.
 */
public class ActionSelectFragment extends Fragment implements SitemapSelectorFragment.OnSitemapSelectListener {

    private static final int MENU_ITEMS = 1;
    private static final int MENU_SITEMAP = 2;

    private static final int REQUEST_SITEMAP = 2;

    public ActionSelectFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tasker_init, container, false);

        MenuAdapter menuAdapter = new MenuAdapter(getActivity());
        menuAdapter.addItem(new MenuItem(getActivity().getString(R.string.items), MENU_ITEMS, R.drawable.ic_icon_action_item));
        menuAdapter.addItem(new MenuItem(getActivity().getString(R.string.open_sitemap), MENU_SITEMAP, R.drawable.ic_icon_sitemap));

        menuAdapter.setOnItemSelectListener(new MenuAdapter.OnItemSelectListener() {
            @Override
            public void itemClicked(int id) {
                switch (id) {
                    case MENU_ITEMS:
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(container.getId(), ItemFragment.newInstance())
                                .addToBackStack(null)
                                .commit();
                        break;
                    case MENU_SITEMAP:
                        Fragment fragment = SitemapSelectorFragment.newInstance();
                        fragment.setTargetFragment(ActionSelectFragment.this, REQUEST_SITEMAP);
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(container.getId(), fragment)
                                .addToBackStack(null)
                                .commit();
                        break;
                }
            }
        });

        RecyclerView lstItems = (RecyclerView) rootView.findViewById(R.id.list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        lstItems.setLayoutManager(gridLayoutManager);
        lstItems.setItemAnimator(new DefaultItemAnimator());
        lstItems.setAdapter(menuAdapter);

        return rootView;
    }

    @Override
    public void onSitemapSelect(Sitemap sitemap) {
        final Intent resultIntent = new Intent();

        SitemapDB sitemapDB = new SitemapDB(sitemap);
        sitemapDB.save();

        final Bundle resultBundle = OpenSitemapBoundleManager.generateOpenSitemapBundle(sitemapDB);
        resultIntent.putExtra(treehou.se.habit.tasker.locale.Intent.EXTRA_STRING_BLURB, getString(R.string.open_sitemap_name, sitemapDB.getName()));
        resultIntent.putExtra(treehou.se.habit.tasker.locale.Intent.EXTRA_BUNDLE, resultBundle);

        getActivity().setResult(Activity.RESULT_OK, resultIntent);
        getActivity().finish();
    }
}
