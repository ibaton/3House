package treehou.se.habit.ui.util;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.typeface.IIcon;

import java.util.ArrayList;
import java.util.List;

import treehou.se.habit.R;

public class IconPickerFragment extends Fragment {

    private RecyclerView lstIcons;
    private CategoryAdapter adapter;

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
        lstIcons.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        adapter = new CategoryAdapter(getActivity());
        adapter.add(new CategoryPicker(GoogleMaterial.Icon.gmd_play_arrow, getString(R.string.media)));
        adapter.add(new CategoryPicker(GoogleMaterial.Icon.gmd_alarm, getString(R.string.sensor)));

        lstIcons.setAdapter(adapter);

        return rootView;
    }

    private class CategoryPicker {

        private IIcon icon;
        private String category;

        public CategoryPicker(IIcon icon, String category) {
            this.icon = icon;
            this.category = category;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public IIcon getIcon() {
            return icon;
        }

        public void setIcon(IIcon icon) {
            this.icon = icon;
        }
    }

    private class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private List<CategoryPicker> categories = new ArrayList<>();


        class CategoryHolder extends RecyclerView.ViewHolder {

            public ImageView imgIcon;
            public TextView lblCategory;

            public CategoryHolder(View itemView) {
                super(itemView);

                imgIcon = (ImageView) itemView.findViewById(R.id.img_item);
                lblCategory = (TextView) itemView.findViewById(R.id.lbl_label);
            }
        }

        public CategoryAdapter(Context context) {
            this.context = context;
        }

        public void add(CategoryPicker category){
            categories.add(category);
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View itemView = inflater.inflate(R.layout.item_menu, null);

            return new CategoryHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {}

        @Override
        public int getItemCount() {
            return categories.size();
        }
    }
}
