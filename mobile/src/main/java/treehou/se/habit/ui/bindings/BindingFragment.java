package treehou.se.habit.ui.bindings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import se.treehou.ng.ohcommunicator.util.GsonHelper;
import se.treehou.ng.ohcommunicator.connector.models.OHBinding;
import treehou.se.habit.R;

public class BindingFragment extends Fragment {

    private static final String ARG_BINDING = "ARG_BINDING";

    @BindView(R.id.lbl_name) TextView lblName;
    @BindView(R.id.lbl_author) TextView lblAuthor;
    @BindView(R.id.lbl_description) TextView lblDescription;

    private OHBinding binding;
    private Unbinder unbinder;

    public static BindingFragment newInstance(OHBinding binding) {
        BindingFragment fragment = new BindingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BINDING, GsonHelper.createGsonBuilder().toJson(binding));
        fragment.setArguments(args);
        return fragment;
    }

    public BindingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if(getArguments() != null){
            if(getArguments().containsKey(ARG_BINDING)){
                binding = GsonHelper.createGsonBuilder().fromJson(getArguments().getString(ARG_BINDING), OHBinding.class);
            }
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_binding, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        lblName.setText(binding.getName());
        lblAuthor.setText(binding.getAuthor());
        lblDescription.setText(binding.getDescription());

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    unbinder.unbind();
    }
}
