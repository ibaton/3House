package treehou.se.habit.ui.control;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import treehou.se.habit.R;

/**
* Created by ibaton on 2014-11-09.
*/
public class IconAdapter extends BaseAdapter {

    //private CellSwitchConfigFragment cellSwitchConfigFragment;
    private Context context;
    private ArrayList<Icon> icons;

    public IconAdapter(Context context) {
        super();

        this.context = context;
        icons = new ArrayList<>();

        String[] names = context.getResources().getStringArray(R.array.cell_icons_name);
        TypedArray resources = context.getResources().obtainTypedArray(R.array.cell_icons);
        int[] values = context.getResources().getIntArray(R.array.cell_icons_values);

        for(int i=0; i<names.length; i++){
            Icon icon = new Icon(names[i], values[i], resources.getResourceId(i,-1));
            icons.add(icon);
        }
    }

    public int getIndexOf(Icon icon){
        return icons.indexOf(icon);
    }

    @Override
    public int getCount() {
        return icons.size();
    }

    @Override
    public Object getItem(int position) {
        return icons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);

        Icon icon = icons.get(position);
        View rootView = inflater.inflate(R.layout.item_image,null);

        ImageView imgIcon = (ImageView) rootView.findViewById(R.id.img_icon);
        imgIcon.setImageResource(icon.getResource());

        TextView louName = (TextView) rootView.findViewById(R.id.lbl_icon_label);
        louName.setText(icon.getName());

        return rootView;
    }
}
