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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import se.treehou.ng.ohcommunicator.util.GsonHelper;
import se.treehou.ng.ohcommunicator.connector.models.OHBinding;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback;
import se.treehou.ng.ohcommunicator.services.callbacks.OHResponse;
import treehou.se.habit.R;
import treehou.se.habit.connector.models.Binding;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.ui.adapter.BindingAdapter;

public class BindingsFragment extends Fragment {

    private static final String TAG = BindingsFragment.class.getSimpleName();

    private static final String ARG_SERVER = "ARG_SERVER";

    private static final String STATE_BINDINGS = "STATE_BINDINGS";

    @BindView(R.id.lst_bindings) RecyclerView lstBinding;

    private BindingAdapter bindingAdapter;
    private ServerDB server;
    private ViewGroup container;

    private List<OHBinding> bindings = new ArrayList<>();

    private Realm realm;
    private Unbinder unbinder;

    private OHCallback<List<OHBinding>> bindingListener = new OHCallback<List<OHBinding>>(){

        @Override
        public void onUpdate(OHResponse<List<OHBinding>> response) {
            Log.d(TAG, "onUpdate " + response.body());
            bindings = response.body();
            bindingAdapter.setBindings(bindings);
        }

        @Override
        public void onError() {
            Log.d(TAG, "onError");
        }
    };

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
        IServerHandler serverHandler = new Connector.ServerHandler(server.toGeneric(), getActivity());
        serverHandler.requestBindings(bindingListener);
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
