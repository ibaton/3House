package treehou.se.habit.ui.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;

import java.util.ArrayList;
import java.util.List;

import treehou.se.habit.R;
import treehou.se.habit.util.Util;

public class IconPickerFragment extends Fragment {

    public static final String ARG_CATEGORY = "ARG_CATEGORY";
    public static final String RESULT_ICON = "RESULT_ICON";

    private RecyclerView lstIcons;
    private IconAdapter adapter;

    public static IconPickerFragment newInstance() {
        IconPickerFragment fragment = new IconPickerFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public IconPickerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_icon_picker, null);
        lstIcons = (RecyclerView) rootView.findViewById(R.id.lst_categories);
        lstIcons.setItemAnimator(new DefaultItemAnimator());
        lstIcons.setLayoutManager(new GridLayoutManager(getActivity(), 4));

        adapter = new IconAdapter(getActivity());
        if(getArguments() != null){
            List<IIcon> icons = Util.CAT_ICONS.get((Util.IconCategory) getArguments().getSerializable(ARG_CATEGORY));
            for(IIcon icon : icons) {
                adapter.add(icon);
            }
        }

        lstIcons.setAdapter(adapter);


        return rootView;
    }

    private class IconAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private List<IIcon> icons = new ArrayList<>();


        class IconHolder extends RecyclerView.ViewHolder {

            public ImageView imgIcon;

            public IconHolder(View itemView) {
                super(itemView);

                imgIcon = (ImageView) itemView.findViewById(R.id.img_menu);
            }
        }

        public IconAdapter(Context context) {
            this.context = context;
        }

        public void add(IIcon icon){
            icons.add(icon);
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View itemView = inflater.inflate(R.layout.item_icon, parent, false);

            return new IconHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            final IIcon item = icons.get(position);
            IconHolder catHolder = (IconHolder) holder;

            IconicsDrawable drawable = new IconicsDrawable(getActivity(), item).color(Color.BLACK).sizeDp(50);

            catHolder.imgIcon.setImageDrawable(drawable);

            catHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra(RESULT_ICON, item.getName());

                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return icons.size();
        }
    }

    public static IconPickerFragment newInstance(Util.IconCategory category) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_CATEGORY, category);

        IconPickerFragment fragment = new IconPickerFragment();
        fragment.setArguments(args);

        return fragment;
    }
}
