package treehou.se.habit.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.connector.GsonHelper;
import treehou.se.habit.connector.models.Binding;
import treehou.se.habit.core.db.ServerDB;

public class BindingsFragment extends Fragment {

    private static final String ARG_SERVER = "ARG_SERVER";

    private static final String STATE_BINDINGS = "STATE_BINDINGS";

    private BindingAdapter bindingAdapter;
    private ServerDB serverDB;
    private ViewGroup container;

    private List<Binding> bindings = new ArrayList<>();

    public static BindingsFragment newInstance(ServerDB serverDB) {
        BindingsFragment fragment = new BindingsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SERVER, serverDB.getId());
        fragment.setArguments(args);
        return fragment;
    }

    public BindingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if(getArguments() != null){
            if(getArguments().containsKey(ARG_SERVER)){
                serverDB = ServerDB.load(ServerDB.class, getArguments().getLong(ARG_SERVER));
            }
        }

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(STATE_BINDINGS)){
                bindings = GsonHelper.createGsonBuilder().fromJson(savedInstanceState.getString(STATE_BINDINGS), new TypeToken<List<Binding>>() {
                }.getType());
            }
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_bindings_list, container, false);

        this.container = container;

        if(serverDB == null){
            Toast.makeText(getActivity(), getString(R.string.failed_to_load_server), Toast.LENGTH_LONG).show();
            getActivity().getSupportFragmentManager().popBackStackImmediate();
            return rootView;
        }

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

        Communicator.instance(getActivity()).listBindings(serverDB, new Callback<List<Binding>>() {
            @Override
            public void success(List<Binding> newBindings, Response response) {
                bindings = newBindings;
                bindingAdapter.setBindings(bindings);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), R.string.failed_list_bindings, Toast.LENGTH_LONG).show();
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_BINDINGS, GsonHelper.createGsonBuilder().toJson(bindings));

        super.onSaveInstanceState(outState);
    }

    public class BindingAdapter extends RecyclerView.Adapter<BindingAdapter.BindingHolder>{

        private List<Binding> bindings = new ArrayList<>();

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

            final Binding binding = bindings.get(position);
            holder.lblName.setText(binding.getName());
            holder.lblAuthor.setText(binding.getAuthor());
            holder.lblDescription.setText(binding.getDescription());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Fragment fragment = BindingFragment.newInstance(serverDB, binding);


                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(container.getId(), fragment)
                            .addToBackStack(null);
                    transaction.commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            return bindings.size();
        }

        public void addBinding(Binding binding){
            bindings.add(binding);
            notifyItemInserted(bindings.size()-1);
        }

        public void setBindings(List<Binding> newBindings){
            bindings.clear();
            bindings.addAll(newBindings);
            notifyDataSetChanged();
        }
    }
}
