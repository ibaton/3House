package treehou.se.habit.ui.control;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import javax.inject.Inject;

import treehou.se.habit.BaseActivity;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.controller.ControllerDB;
import treehou.se.habit.ui.colorpicker.ColorDialog;

public class EditControllerSettingsActivity extends BaseActivity implements ColorDialog.ColorDialogCallback {

    public static final String ARG_ID = "ARG_ID";

    private EditText txtName;
    private Button btnColor;

    private CheckBox cbxAsNotification;

    @Inject ControllerUtil controllerUtil;

    private ControllerDB controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((HabitApplication)getApplicationContext()).component().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_controller_settings);

        if (getIntent().getExtras() != null) {
            long id = getIntent().getExtras().getLong(ARG_ID);
            controller = ControllerDB.load(realm, id);
        }

        txtName = (EditText) findViewById(R.id.txt_name);
        txtName.setText(controller.getName());

        btnColor = (Button) findViewById(R.id.btn_color);
        btnColor.setOnClickListener(v -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = ColorDialog.instance();
            fragmentManager.beginTransaction()
                    .add(fragment, "colordialog")
                    .commit();
        });

        cbxAsNotification = (CheckBox) findViewById(R.id.as_notification);
        cbxAsNotification.setChecked(controller.isShowNotification());
        cbxAsNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            realm.beginTransaction();
            controller.setShowNotification(isChecked);
            realm.commitTransaction();
        });

        updateColorPalette(controller.getColor());

        findViewById(R.id.container).setOnClickListener(v -> finish());
    }

    /**
     * Update ui to match color set.
     *
     * @param color the color to use as base.
     */
    public void updateColorPalette(int color){

        btnColor.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    @Override
    protected void onPause() {
        super.onPause();

        realm.beginTransaction();
        controller.setName(txtName.getText().toString());
        realm.commitTransaction();

        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void setColor(int color) {
        realm.beginTransaction();
        controller.setColor(color);
        realm.commitTransaction();
        btnColor.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }
}
