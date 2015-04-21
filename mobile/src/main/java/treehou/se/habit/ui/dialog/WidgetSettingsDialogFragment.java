package treehou.se.habit.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import treehou.se.habit.R;

/**
 * Created by ibaton on 2015-03-19.
 */
public class WidgetSettingsDialogFragment extends DialogFragment {

    public static int ITEM_CUSTOM = 1;

    private OnItemSelectedListener itemSeletedDummy = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(int itemId) {}
    };
    private OnItemSelectedListener itemListener = itemSeletedDummy;

    private List<MenuItem> items;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        items = new ArrayList<>();
        items.add(new MenuItem(getString(R.string.custom_widgets), ITEM_CUSTOM));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        ListView list = new ListView(getActivity());
        ArrayAdapter<MenuItem> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, items);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MenuItem item = items.get(position);
                itemSeletedDummy.onItemSelected(item.id);
                dismiss();
            }
        });
        builder.setView(list);

        builder.setMessage(getActivity().getString(R.string.widger_settings));
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        if(listener == null){
            itemListener = itemSeletedDummy;
        }else{
            itemListener = listener;
        }
    }

    public interface OnItemSelectedListener {
        public void onItemSelected(int itemId);
    }

    public class MenuItem {
        public String text;
        public int id;

        public MenuItem(String text, int id) {
            this.text = text;
            this.id = id;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
