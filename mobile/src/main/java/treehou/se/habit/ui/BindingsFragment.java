package treehou.se.habit.ui;

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
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import org.atmosphere.wasync.Socket;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.connector.GsonHelper;
import se.treehou.ng.ohcommunicator.connector.models.OHBinding;
import se.treehou.ng.ohcommunicator.services.callbacks.OHCallback;
import se.treehou.ng.ohcommunicator.services.callbacks.OHResponse;
import treehou.se.habit.R;
import treehou.se.habit.connector.models.Binding;
import treehou.se.habit.core.db.model.ServerDB;

public class BindingsFragment extends Fragment {

    private static final String TAG = BindingsFragment.class.getSimpleName();

    private static final String ARG_SERVER = "ARG_SERVER";

    private static final String STATE_BINDINGS = "STATE_BINDINGS";

    private BindingAdapter bindingAdapter;
    private ServerDB server;
    private ViewGroup container;

    private List<OHBinding> bindings = new ArrayList<>();

    private Realm realm;

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

    public static BindingsFragment newInstance(ServerDB server) {
        BindingsFragment fragment = new BindingsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SERVER, server.getId());
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
        this.container = container;

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(R.string.bindings);
        }

        final RecyclerView lstBinding = (RecyclerView) rootView.findViewById(R.id.lst_bindings);
        bindingAdapter = new BindingAdapter();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        lstBinding.setLayoutManager(gridLayoutManager);
        lstBinding.setItemAnimator(new DefaultItemAnimator());
        lstBinding.setAdapter(bindingAdapter);
        bindingAdapter.setBindings(bindings);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Openhab.instance(server.toGeneric()).requestBindings(bindingListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_BINDINGS, GsonHelper.createGsonBuilder().toJson(bindings));

        super.onSaveInstanceState(outState);
    }

    public class BindingAdapter extends RecyclerView.Adapter<BindingAdapter.BindingHolder>{

        private List<OHBinding> bindings = new ArrayList<>();

        public class BindingHolder extends RecyclerView.ViewHolder {

            private TextView lblName;
            private TextView lblAuthor;
            private TextView lblDescription;

            public BindingHolder(View itemView) {
                super(itemView);

                lblName = (TextView) itemView.findViewById(R.id.lbl_name);
                lblAuthor = (TextView) itemView.findViewById(R.id.lbl_author);
                lblDescription = (TextView) itemView.findViewById(R.id.lbl_description);
            }
        }

        public BindingAdapter() {
        }

        @Override
        public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View itemView = inflater.inflate(R.layout.item_binding, null);

            return new BindingHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final BindingHolder holder, int position) {

            final OHBinding binding = bindings.get(position);
            holder.lblName.setText(binding.getName());
            holder.lblAuthor.setText(binding.getAuthor());
            holder.lblDescription.setText(binding.getDescription());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Fragment fragment = BindingFragment.newInstance(binding);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(container.getId(), fragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            return bindings.size();
        }

        public void addBinding(OHBinding binding){
            bindings.add(binding);
            notifyItemInserted(bindings.size()-1);
        }

        public void setBindings(List<OHBinding> newBindings){
            bindings.clear();
            bindings.addAll(newBindings);
            notifyDataSetChanged();
        }
    }
}
