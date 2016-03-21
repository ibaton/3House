package treehou.se.habit.ui.control;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import treehou.se.habit.R;
import treehou.se.habit.core.db.model.controller.ControllerDB;
import treehou.se.habit.ui.colorpicker.ColorDialog;

public class EditControllerSettingsActivity extends AppCompatActivity implements ColorDialog.ColorDialogCallback {

    public static final String ARG_ID = "ARG_ID";

    private EditText txtName;
    private Button btnColor;

    private CheckBox cbxAsNotification;

    private ControllerDB controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_controller_settings);

        if (getIntent().getExtras() != null) {
            int id = getIntent().getExtras().getInt(ARG_ID);
            controller = null;//ControllerDB.load(id);
        }

        txtName = (EditText) findViewById(R.id.txt_name);
        txtName.setText(controller.getName());

        btnColor = (Button) findViewById(R.id.btn_color);
        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();

                Fragment fragment = ColorDialog.instance();

                fragmentManager.beginTransaction()
                        .add(fragment, "colordialog")
                        .commit();
            }
        });

        cbxAsNotification = (CheckBox) findViewById(R.id.as_notification);
        cbxAsNotification.setChecked(controller.isShowNotification());
        cbxAsNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                controller.setShowNotification(isChecked);
                //ControllerDB.save(controller);

                if(controller.isShowNotification()) {
                    ControlHelper.showNotification(EditControllerSettingsActivity.this, controller);
                }else {
                    ControlHelper.hideNotification(EditControllerSettingsActivity.this, controller);
                }
            }
        });

        updateColorPalette(controller.getColor());

        findViewById(R.id.container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void save(){
        controller.setName(txtName.getText().toString());
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

        save();

        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void setColor(int color) {
        controller.setColor(color);
        //ControllerDB.save(controller);

        btnColor.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }
}
