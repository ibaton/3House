package treehou.se.habit.ui.control

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText

import javax.inject.Inject

import treehou.se.habit.BaseActivity
import treehou.se.habit.HabitApplication
import treehou.se.habit.R
import treehou.se.habit.core.db.model.controller.ControllerDB
import treehou.se.habit.ui.colorpicker.ColorDialog

class EditControllerSettingsActivity : BaseActivity(), ColorDialog.ColorDialogCallback {

    private lateinit var txtName: EditText
    private lateinit var btnColor: Button

    private lateinit var cbxAsNotification: CheckBox

    @Inject lateinit var controllerUtil: ControllerUtil

    private var controller: ControllerDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as HabitApplication).component().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_controller_settings)

        if (intent.extras != null) {
            val id = intent.extras!!.getLong(ARG_ID)
            controller = ControllerDB.load(realm, id)
        }

        txtName = findViewById(R.id.txt_name)
        txtName.setText(controller!!.name)

        btnColor = findViewById(R.id.btn_color)
        btnColor.setOnClickListener {
            val fragmentManager = supportFragmentManager
            val fragment = ColorDialog.instance()
            fragmentManager.beginTransaction()
                    .add(fragment, "colordialog")
                    .commit()
        }

        cbxAsNotification = findViewById(R.id.as_notification)
        cbxAsNotification.isChecked = controller!!.showNotification
        cbxAsNotification.setOnCheckedChangeListener { _, isChecked ->
            realm.beginTransaction()
            controller!!.showNotification = isChecked
            realm.commitTransaction()
        }

        updateColorPalette(controller!!.color)

        findViewById<View>(R.id.container).setOnClickListener({ finish() })
    }

    /**
     * Update ui to match color set.
     *
     * @param color the color to use as base.
     */
    fun updateColorPalette(color: Int) {
        btnColor.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }

    override fun onPause() {
        super.onPause()

        realm.beginTransaction()
        controller!!.name = txtName.text.toString()
        realm.commitTransaction()

        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    override fun setColor(color: Int) {
        realm.beginTransaction()
        controller!!.color = color
        realm.commitTransaction()
        btnColor.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }

    companion object {

        val ARG_ID = "ARG_ID"
    }
}
