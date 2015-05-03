package treehou.se.habit.ui.colorpicker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import treehou.se.habit.R;

public class ColorDialog extends DialogFragment {

    public static final String EXTRA_COLOR = "color";

    public static ColorDialog instance(){
        return new ColorDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View pickerView = inflater.inflate(R.layout.color_picker, null);

        AbsListView lstColors = (AbsListView) pickerView.findViewById(R.id.lst_colors);

        TypedArray ta = getResources().obtainTypedArray(R.array.cell_colors);
        ArrayList<Integer> colors = new ArrayList<>();
        for(int i=0; i < ta.length(); i++) colors.add(ta.getColor(i, Color.TRANSPARENT));
        ta.recycle();

        lstColors.setAdapter(new ColorAdapter(getActivity(), R.layout.item_color, colors));
        lstColors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ColorDialogCallback colorCallback = (ColorDialogCallback) getTargetFragment();
                colorCallback.setColor((int)view.getTag());
                ColorDialog.this.dismiss();
            }
        });

        builder.setView(pickerView)
                .setNegativeButton(getActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ColorDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    class ColorAdapter extends ArrayAdapter<Integer> {

        ColorAdapter(Context context, int resource, List<Integer> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            int color = getItem(position);

            View rootView = inflater.inflate(R.layout.item_color, null);
            rootView.setTag(color);

            View viwColor = rootView.findViewById(R.id.viw_color);
            viwColor.setBackgroundColor(color);

            return rootView;
        }
    }

    public interface ColorDialogCallback{
        public void setColor(int color);
    }
}
