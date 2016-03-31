package treehou.se.habit.ui.control;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import io.realm.Realm;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.controller.CellDB;
import treehou.se.habit.core.db.model.controller.CellRowDB;
import treehou.se.habit.core.db.model.controller.ControllerDB;
import treehou.se.habit.ui.colorpicker.ColorDialog;
import treehou.se.habit.ui.control.config.ControllCellFragment;
import treehou.se.habit.ui.control.config.cells.ButtonConfigCellBuilder;
import treehou.se.habit.ui.control.config.cells.ColorConfigCellBuilder;
import treehou.se.habit.ui.control.config.cells.DefaultConfigCellBuilder;
import treehou.se.habit.ui.control.config.cells.IncDecConfigCellBuilder;
import treehou.se.habit.ui.control.config.cells.SliderConfigCellBuilder;
import treehou.se.habit.ui.control.config.cells.VoiceConfigCellBuilder;
import treehou.se.habit.ui.homescreen.ControllerWidget;

public class EditControlFragment extends Fragment implements ColorDialog.ColorDialogCallback {

    private static final String TAG = "EditControlFragment";

    public static final String ARG_ID = "ARG_ID";

    public static final int REQUEST_COLOR = 3117;

    private LinearLayout louController;
    private View titleHolder;
    private View viwBackground;

    private DrawerLayout drwLayout;

    private ActionBar actionBar;

    private ControllerDB controller;

    private AppCompatActivity activity;

    private CellFactory<Integer> cellFactory;

    private Realm realm;

    public static EditControlFragment newInstance(long id) {
        EditControlFragment fragment = new EditControlFragment();
        Bundle args = new Bundle();

        args.putLong(ARG_ID, id);

        fragment.setArguments(args);
        return fragment;
    }
    public EditControlFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();

        activity = (AppCompatActivity) getActivity();

        cellFactory = new CellFactory<>();
        cellFactory.setDefaultBuilder(new DefaultConfigCellBuilder());
        cellFactory.addBuilder(CellDB.TYPE_BUTTON, new ButtonConfigCellBuilder());
        cellFactory.addBuilder(CellDB.TYPE_VOICE, new VoiceConfigCellBuilder());
        cellFactory.addBuilder(CellDB.TYPE_SLIDER, new SliderConfigCellBuilder());
        cellFactory.addBuilder(CellDB.TYPE_INC_DEC, new IncDecConfigCellBuilder());
        cellFactory.addBuilder(CellDB.TYPE_COLOR, new ColorConfigCellBuilder());

        if (getArguments() != null) {
            long id = getArguments().getLong(ARG_ID);
            controller = ControllerDB.load(realm, id);
        }

        ControlHelper.showNotification(getActivity(), controller);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_control, container, false);

        actionBar = activity.getSupportActionBar();
        drwLayout = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
        louController = (LinearLayout) rootView.findViewById(R.id.lou_btn_holder);
        viwBackground = rootView.findViewById(R.id.viw_background);

        updateColorPalette(controller.getColor());

        ImageButton btnAddRow = (ImageButton) rootView.findViewById(R.id.btn_add_row);
        btnAddRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.addRow(realm);
                Log.d("Controller", "Added controller, currently " + controller.getCellRows().size() + " rows");
                redrawController();
            }
        });
        redrawController();

        Intent i = new Intent("treehou.se.UPDATE_WIDGET");
        getActivity().sendBroadcast(i);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_controllers, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_more:
                openExtraSettings();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openExtraSettings(){
        Intent intent = new Intent(getActivity(), EditControllerSettingsActivity.class);
        Bundle extras = new Bundle();

        extras.putLong(ARG_ID, controller.getId());
        intent.putExtras(extras);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        startActivity(intent);
        getActivity().overridePendingTransition(0, 0);
    }

    @Override
    public void onResume() {
        super.onResume();

        redrawController();
    }

    @Override
    public void onPause() {
        super.onPause();

        Intent intent = new Intent(getActivity(), ControllerWidget.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        Activity activity = getActivity();
        int ids[] = AppWidgetManager.getInstance(activity.getApplication()).getAppWidgetIds(new ComponentName(activity.getApplication(), ControllerWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        getActivity().sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        realm.close();
    }

    public void redrawController(){

        ControlHelper.showNotification(getActivity(), controller);

        louController.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        Log.d(TAG, "Drawing controller " + controller.getCellRows().size());
        for (final CellRowDB row : controller.getCellRows()){
            Log.d(TAG, "Drawing row " + row.getId());
            final LinearLayout louRow = (LinearLayout) inflater.inflate(R.layout.controller_row_edit, null);

            LinearLayout.LayoutParams rowParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            rowParam.weight = 1;
            louRow.setLayoutParams(rowParam);

            final LinearLayout louColumnHolder = (LinearLayout) louRow.findViewById(R.id.lou_btn_holder);
            final ImageButton btnAddCell = (ImageButton) louRow.findViewById(R.id.btn_add_column);

            for (final CellDB cell : row.getCells()) {
                Log.d(TAG, "Drawing cell " + cell.getId());
                final View itemView = cellFactory.create(getActivity(), controller, cell);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.page_container, ControllCellFragment.newInstance(cell.getId()))
                                .addToBackStack(null)
                                .commit();
                    }
                });
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new AlertDialog.Builder(getActivity())
                                .setMessage(activity.getString(R.string.delete_cell))
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        realm.beginTransaction();
                                        cell.removeFromRealm();
                                        realm.commitTransaction();
                                        redrawController();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, null)
                                .show();

                        return true;
                    }
                });
                louColumnHolder.addView(itemView);
            }

            btnAddCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    row.addCell(realm);
                    redrawController();
                }
            });
            louController.addView(louRow);
        }
    }

    /**
     * Update ui to match color set.
     *
     * @param color the color to use as base.
     */
    public void updateColorPalette(int color){

        /*int[] pallete;
        if(Colour.alpha(color) < 100){
            pallete = Util.generatePallete(getResources().getColor(R.color.colorPrimary));
        }else{
            pallete = Util.generatePallete(color);
        }

        btnColor.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        viwBackground.setBackgroundColor(pallete[0]);
        titleHolder.setBackgroundColor(pallete[0]);
        lblSettingsContainer.setBackgroundColor(pallete[0]);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            activity.getWindow().setStatusBarColor(pallete[0]);
            activity.getWindow().setNavigationBarColor(pallete[0]);
            if(actionBar != null) {
                actionBar.setBackgroundDrawable(new ColorDrawable(pallete[0]));
            }
        }*/

        redrawController();
    }

    @Override
    public void onDestroyView() {

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
            activity.getWindow().setNavigationBarColor(activity.getResources().getColor(R.color.navigationBarColor));
            if(actionBar != null) {
                actionBar.setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(R.color.colorPrimary)));
            }
        }
        ControlHelper.showNotification(getActivity(), controller);

        super.onDestroyView();
    }

    @Override
    public void setColor(int color) {
        controller.setColor(color);
        updateColorPalette(color);
    }
}
