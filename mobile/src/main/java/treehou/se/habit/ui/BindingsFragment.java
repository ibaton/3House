package treehou.se.habit.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.connector.models.Binding;
import treehou.se.habit.core.db.ServerDB;

public class BindingsFragment extends Fragment {

    private static final String ARG_SERVER = "ARG_SERVER";

    private BindingAdapter bindingAdapter;
    private ServerDB serverDB;

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

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_bindings_list, container, false);

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
        bindingAdapter = new BindingAdapter(getActivity());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        lstBinding.setLayoutManager(gridLayoutManager);
        lstBinding.setItemAnimator(new DefaultItemAnimator());
        lstBinding.setAdapter(bindingAdapter);

        setHasOptionsMenu(true);

        Communicator.instance(getActivity()).listBindings(serverDB, new Callback<List<Binding>>() {
            @Override
            public void success(List<Binding> bindings, Response response) {
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

    public static class BindingAdapter extends RecyclerView.Adapter<BindingAdapter.BindingHolder>{

        private Context context;
        private List<Binding> bindings = new ArrayList<>();

        public static class BindingHolder extends RecyclerView.ViewHolder {

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

        public BindingAdapter(Context context) {
            this.context = context;
        }

        @Override
        public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(context);
            View itemView = inflater.inflate(R.layout.item_binding, null);

            return new BindingHolder(itemView);
        }

        @Override
        public void onBindViewHolder(BindingHolder holder, int position) {

            Binding binding = bindings.get(position);
            holder.lblName.setText(binding.getName());
            holder.lblAuthor.setText(binding.getAuthor());
            holder.lblDescription.setText(binding.getDescription());
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
            bindings.addAll(newBindings);
            notifyDataSetChanged();
        }
    }
}
