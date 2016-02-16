package treehou.se.habit.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import se.treehou.ng.ohcommunicator.connector.models.ThingType;
import se.treehou.ng.ohcommunicator.core.OHBinding;
import treehou.se.habit.R;
import treehou.se.habit.connector.GsonHelper;

public class BindingFragment extends Fragment {

    private static final String ARG_BINDING = "ARG_BINDING";

    private OHBinding binding;

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

        TextView lblName = (TextView) rootView.findViewById(R.id.lbl_name);
        TextView lblAuthor = (TextView) rootView.findViewById(R.id.lbl_author);
        TextView lblDescription = (TextView) rootView.findViewById(R.id.lbl_description);
        LinearLayout louChannels = (LinearLayout) rootView.findViewById(R.id.lou_thing_types);

        lblName.setText(binding.getName());
        lblAuthor.setText(binding.getAuthor());
        lblDescription.setText(binding.getDescription());

        louChannels.removeAllViews();
        for(ThingType thingType : binding.getThingTypes()){
            View itemThingType = inflater.inflate(R.layout.item_thing_type, louChannels, false);
            TextView lblThingName = (TextView) itemThingType.findViewById(R.id.lbl_name);
            TextView lblThingDescription = (TextView) itemThingType.findViewById(R.id.lbl_description);

            lblThingName.setText(thingType.getLabel());
            lblThingDescription.setText(thingType.getDescription());

            louChannels.addView(itemThingType);
        }

        return rootView;
    }
}
