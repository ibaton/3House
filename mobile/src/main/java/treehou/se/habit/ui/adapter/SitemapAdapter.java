package treehou.se.habit.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.ui.sitemaps.PageFragment;

public class SitemapAdapter extends FragmentStatePagerAdapter {

    private List<OHLinkedPage> mPages;
    private ServerDB mServer;

    public SitemapAdapter(ServerDB server, FragmentManager fragmentManager, List<OHLinkedPage> pages){
        super(fragmentManager);

        mPages = pages;
        mServer = server;
    }

    @Override
    public Fragment getItem(int i) {
        return PageFragment.newInstance(mServer, mPages.get(i));
    }

    @Override
    public int getCount() {
        return mPages.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mPages.get(position).getTitle();
    }

    @Override
    public int getItemPosition(Object object){
        return FragmentStatePagerAdapter.POSITION_NONE;
    }
}
