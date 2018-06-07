package treehou.se.habit.ui.control.cells.config

import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_controll_cell.*
import treehou.se.habit.R
import treehou.se.habit.core.db.model.controller.CellDB
import treehou.se.habit.ui.colorpicker.ColorDialog

class ControllCellFragment : Fragment(), ColorDialog.ColorDialogCallback {

    private var mTypeAdapter: ArrayAdapter<*>? = null
    private var cell: CellDB? = null

    private lateinit var realm: Realm

    private val itemSelectListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

            val cellType = resources.getIntArray(R.array.cell_types_values)[position]

            Log.d(TAG, "item selected $cellType $position")

            val fragmentManager = activity?.supportFragmentManager ?: return

            var fragment: Fragment? = null
            val cell = cell
            if (cell != null) {
                when (cellType) {
                    CellDB.TYPE_BUTTON -> {
                        Log.d(TAG, "Loading button fragment.")
                        fragment = CellButtonConfigFragment.newInstance(cell)
                    }
                    CellDB.TYPE_SLIDER -> {
                        Log.d(TAG, "Loading slider fragment.")
                        fragment = CellSliderConfigFragment.newInstance(cell)
                    }
                    CellDB.TYPE_VOICE -> {
                        Log.d(TAG, "Loading voice fragment.")
                        fragment = CellVoiceConfigFragment.newInstance(cell)
                    }
                    CellDB.TYPE_INC_DEC -> {
                        Log.d(TAG, "Loading IncDec fragment.")
                        fragment = CellIncDecConfigFragment.newInstance(cell)
                    }
                    else -> {
                        Log.d(TAG, "Loading empty fragment.")
                        val currentFragment = fragmentManager.findFragmentById(R.id.lou_config_container)
                        if (currentFragment != null) {
                            fragmentManager.beginTransaction()
                                    .remove(currentFragment)
                                    .commit()
                        }
                    }
                }
            }

            if (fragment != null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.lou_config_container, fragment)
                        .commit()
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        realm = Realm.getDefaultInstance()
        if (arguments != null) {
            val cellId = arguments!!.getLong(ARG_CELL_ID)
            cell = CellDB.load(realm, cellId)
        }

        val cellTypes = resources.getStringArray(R.array.cell_types)
        mTypeAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_dropdown_item, cellTypes)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_controll_cell, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        itemsSpinner.adapter = mTypeAdapter
        itemsSpinner.onItemSelectedListener = itemSelectListener

        updateColorButton(cell!!.color)
        colorPickerButton.setOnClickListener {
            val dialog = ColorDialog.instance()
            dialog.setTargetFragment(this@ControllCellFragment, REQUEST_COLOR)
            activity!!.supportFragmentManager.beginTransaction()
                    .add(dialog, "colordialog")
                    .commit()
        }
        Log.d(TAG, "Color is : " + cell!!.color)

        val typeArray = resources.getIntArray(R.array.cell_types_values)
        var index = 0
        for (i in typeArray.indices) {
            if (typeArray[i] == cell!!.type) {
                index = i
                break
            }
        }
        itemsSpinner.setSelection(index)
    }

    override fun onDestroy() {
        super.onDestroy()

        realm!!.close()
    }

    /**
     * Update the color of color button
     * @param color the color to set
     */
    fun updateColorButton(@ColorInt color: Int) {
        colorPickerButton.setBackgroundColor(color)
    }

    override fun setColor(color: Int) {
        Log.d(TAG, "Color set: " + color)
        updateColorButton(color)

        realm!!.beginTransaction()
        cell!!.color = color
        realm!!.commitTransaction()
    }

    companion object {

        val TAG = "ControllCellFragment"
        val ARG_CELL_ID = "ARG_CELL_ID"
        val REQUEST_COLOR = 3001

        fun newInstance(cellId: Long): ControllCellFragment {
            val fragment = ControllCellFragment()
            val args = Bundle()
            args.putLong(ARG_CELL_ID, cellId)
            fragment.arguments = args

            return fragment
        }
    }
}
