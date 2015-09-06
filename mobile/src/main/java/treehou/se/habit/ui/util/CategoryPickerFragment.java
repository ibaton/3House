package treehou.se.habit.ui.util;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;

import java.util.ArrayList;
import java.util.List;

import treehou.se.habit.R;
import treehou.se.habit.util.Util;

/**
 * Fragment for picking categories of icons.
 */
public class CategoryPickerFragment extends Fragment {

    private RecyclerView lstIcons;
    private CategoryAdapter adapter;

    private ViewGroup container;

    public static CategoryPickerFragment newInstance() {
        CategoryPickerFragment fragment = new CategoryPickerFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public CategoryPickerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_icon_picker, null);
        lstIcons = (RecyclerView) rootView.findViewById(R.id.lst_categories);
        lstIcons.setItemAnimator(new DefaultItemAnimator());
        lstIcons.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Hookup list of categories
        adapter = new CategoryAdapter(getActivity());
        adapter.add(new CategoryPicker(CommunityMaterial.Icon.cmd_play, getString(R.string.media), Util.IconCategory.MEDIA));
        adapter.add(new CategoryPicker(CommunityMaterial.Icon.cmd_alarm, getString(R.string.sensor), Util.IconCategory.SENSORS));
        adapter.add(new CategoryPicker(CommunityMaterial.Icon.cmd_power, getString(R.string.command), Util.IconCategory.COMMANDS));
        adapter.add(new CategoryPicker(CommunityMaterial.Icon.cmd_arrow_up, getString(R.string.arrows), Util.IconCategory.ARROWS));
        adapter.add(new CategoryPicker(CommunityMaterial.Icon.cmd_view_module, getString(R.string.all), Util.IconCategory.ALL));

        lstIcons.setAdapter(adapter);

        this.container = container;

        return rootView;
    }

    private class CategoryPicker {

        private IIcon icon;
        private String category;
        private Util.IconCategory id;

        public CategoryPicker(IIcon icon, String category, Util.IconCategory id) {
            this.icon = icon;
            this.category = category;
            this.id = id;
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

        public Util.IconCategory getId() {
            return id;
        }

        public void setId(Util.IconCategory id) {
            this.id = id;
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

                imgIcon = (ImageView) itemView.findViewById(R.id.img_menu);
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
            View itemView = inflater.inflate(R.layout.item_category, parent, false);

            return new CategoryHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            final CategoryPicker item = categories.get(position);
            CategoryHolder catHolder = (CategoryHolder) holder;

            IconicsDrawable drawable = new IconicsDrawable(getActivity(), item.getIcon()).color(Color.BLACK).sizeDp(50);

            catHolder.imgIcon.setImageDrawable(drawable);
            catHolder.lblCategory.setText(item.getCategory());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(container.getId(), IconPickerFragment.newInstance(item.getId()))
                            .addToBackStack(null)
                            .commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }
    }
}
