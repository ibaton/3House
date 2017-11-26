package treehou.se.habit.ui.servers.sitemaps.sitemapsettings;

import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.jakewharton.rxbinding2.widget.RxCompoundButton;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.realm.Realm;
import io.realm.RealmResults;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.SitemapDB;
import treehou.se.habit.module.HasActivitySubcomponentBuilders;
import treehou.se.habit.mvp.BaseDaggerFragment;
import treehou.se.habit.ui.BaseFragment;
import treehou.se.habit.ui.servers.sitemaps.list.SitemapSelectComponent;
import treehou.se.habit.ui.servers.sitemaps.list.SitemapSelectContract;
import treehou.se.habit.ui.servers.sitemaps.list.SitemapSelectFragment;
import treehou.se.habit.ui.servers.sitemaps.list.SitemapSelectModule;

public class SitemapSettingsFragment extends BaseDaggerFragment<SitemapSettingsContract.Presenter> implements SitemapSettingsContract.View {

    private static final String TAG = "SitemapSelectFragment";

    private static final String ARG_SITEMAP = "ARG_SITEMAP";

    @BindView(R.id.show_in_sitemap) Switch cbxShowSitemaps;

    @Inject SitemapSettingsContract.Presenter presenter;

    private Realm realm;
    private Unbinder unbinder;
    private long sitemapId = -1;

    /**
     * Load sitemaps setting.
     *
     * @param sitemapId the sitemap to load
     * @return Fragment
     */
    public static SitemapSettingsFragment newInstance(long sitemapId) {
        SitemapSettingsFragment fragment = new SitemapSettingsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SITEMAP, sitemapId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SitemapSettingsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();
        sitemapId = getArguments().getLong(ARG_SITEMAP);
    }

    @Override
    public SitemapSettingsContract.Presenter getPresenter() {
        return presenter;
    }

    @Override
    protected void injectMembers(HasActivitySubcomponentBuilders hasActivitySubcomponentBuilders) {
        ((SitemapSettingsComponent.Builder) hasActivitySubcomponentBuilders.getFragmentComponentBuilder(SitemapSettingsFragment.class))
                .fragmentModule(new SitemapSettingsModule(this))
                .build().injectMembers(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sitemap_settings, container, false);
        unbinder = ButterKnife.bind(this, view);
        setupActionBar();

        Observable<SitemapDB> sitemapObservable = realm.where(SitemapDB.class).equalTo("id", sitemapId).findAll().asFlowable().toObservable()
                .flatMap(Observable::fromIterable)
                .filter(sitemapDB -> sitemapDB != null && sitemapDB.getSettingsDB() != null)
                .distinctUntilChanged();

        sitemapObservable.map(sitemapDB -> sitemapDB.getSettingsDB().isDisplay())
                .compose(bindToLifecycle())
                .subscribe(RxCompoundButton.checked(cbxShowSitemaps));

       Observable.combineLatest(sitemapObservable,
                RxCompoundButton.checkedChanges(cbxShowSitemaps), Pair::new)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sitemapDBBooleanPair -> {
                    SitemapDB sitemapDB = sitemapDBBooleanPair.first;
                    boolean showSitemap = sitemapDBBooleanPair.second;
                    realm.beginTransaction();
                    sitemapDB.getSettingsDB().setDisplay(showSitemap);
                    realm.commitTransaction();
                });

        return view;
    }

    /**
     * Setup actionbar.
     */
    private void setupActionBar(){
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) actionBar.setTitle(R.string.sitemaps);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
