package treehou.se.habit.ui.sitemaps;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.realm.Realm;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import treehou.se.habit.module.RxPresenter;
import treehou.se.habit.module.ServerLoaderFactory;
import treehou.se.habit.ui.sitemaps.sitemaplist.SitemapListContract;
import treehou.se.habit.ui.sitemaps.sitemaplist.SitemapListFragment;
import treehou.se.habit.util.Settings;

public class PagePresenter extends RxPresenter implements PageContract.Presenter {

    private static final String TAG = PagePresenter.class.getSimpleName();

    private PageContract.View view;
    private Context context;

    @Inject
    public PagePresenter(PageContract.View view, Context context) {
        this.view = view;
        this.context = context;
    }

    @Override
    public void load(Bundle savedData) {
    }


    @Override
    public void subscribe() {
    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void unload() {
    }

    @Override
    public void save(Bundle savedData) {}
}
