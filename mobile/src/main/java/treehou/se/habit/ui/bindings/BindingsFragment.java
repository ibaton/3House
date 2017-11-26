package treehou.se.habit.ui.bindings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.trello.rxlifecycle2.components.support.RxFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import se.treehou.ng.ohcommunicator.util.GsonHelper;
import se.treehou.ng.ohcommunicator.connector.models.OHBinding;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.R;
import treehou.se.habit.connector.models.Binding;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.ui.adapter.BindingAdapter;
import treehou.se.habit.util.ConnectionFactory;
import treehou.se.habit.util.Util;

public class BindingsFragment extends RxFragment {

    private static final String TAG = BindingsFragment.class.getSimpleName();

    private static final String ARG_SERVER = "ARG_SERVER";

    private static final String STATE_BINDINGS = "STATE_BINDINGS";

    @BindView(R.id.lst_bindings) RecyclerView lstBinding;

    @Inject ConnectionFactory connectionFactory;

    private BindingAdapter bindingAdapter;
    private ServerDB server;
    private ViewGroup container;

    private List<OHBinding> bindings = new ArrayList<>();

    private Realm realm;
    private Unbinder unbinder;

    public static BindingsFragment newInstance(long serverId) {
        BindingsFragment fragment = new BindingsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SERVER, serverId);
        fragment.setArguments(args);
        return fragment;
    }

    public BindingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Util.getApplicationComponent(this).inject(this);
        realm = Realm.getDefaultInstance();

        if(getArguments() != null){
            if(getArguments().containsKey(ARG_SERVER)){
                long serverId = getArguments().getLong(ARG_SERVER);
                server = Realm.getDefaultInstance().where(ServerDB.class).equalTo("id", serverId).findFirst();
            }
        }

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(STATE_BINDINGS)){
                bindings = GsonHelper.createGsonBuilder().fromJson(savedInstanceState.getString(STATE_BINDINGS), new TypeToken<List<Binding>>() {}.getType());
            }
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_bindings_list, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        this.container = container;

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(R.string.bindings);
        }

        bindingAdapter = new BindingAdapter();
        bindingAdapter.setItemClickListener(binding -> openBinding(binding));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        lstBinding.setLayoutManager(gridLayoutManager);
        lstBinding.setItemAnimator(new DefaultItemAnimator());
        lstBinding.setAdapter(bindingAdapter);
        bindingAdapter.setBindings(bindings);

        setHasOptionsMenu(true);

        return rootView;
    }

    /**
     * Open up binding page.
     * @param binding the binding to show.
     */
    private void openBinding(OHBinding binding){
        Fragment fragment = BindingFragment.newInstance(binding);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(BindingsFragment.this.container.getId(), fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        IServerHandler serverHandler = connectionFactory.createServerHandler(server.toGeneric(), getActivity());
        serverHandler.requestBindingsRx()
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bindings -> {
                    Log.d(TAG, "onUpdate " + bindings);
                    bindingAdapter.setBindings(bindings);
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_BINDINGS, GsonHelper.createGsonBuilder().toJson(bindings));

        super.onSaveInstanceState(outState);
    }

}
