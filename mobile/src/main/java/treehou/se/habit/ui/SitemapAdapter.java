package treehou.se.habit.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import se.treehou.ng.ohcommunicator.core.OHLinkedPageWrapper;
import se.treehou.ng.ohcommunicator.core.OHServerWrapper;

public class SitemapAdapter extends FragmentStatePagerAdapter {

    private List<OHLinkedPageWrapper> mPages;
    private OHServerWrapper mServer;

    public SitemapAdapter(OHServerWrapper server, FragmentManager fragmentManager, List<OHLinkedPageWrapper> pages){
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
