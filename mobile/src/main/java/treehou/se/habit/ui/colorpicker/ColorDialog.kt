package treehou.se.habit.ui.colorpicker

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import treehou.se.habit.R
import java.util.*

class ColorDialog : DialogFragment() {

    private var colorCallback: ColorDialogCallback? = null

    override fun onAttach(activity: Activity?) {

        if (targetFragment != null && targetFragment is ColorDialogCallback) {
            colorCallback = targetFragment as ColorDialogCallback?
        } else if (activity is ColorDialogCallback) {
            colorCallback = activity
        }

        super.onAttach(activity)
    }

    override fun onDetach() {
        colorCallback = null

        super.onDetach()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)

        val inflater = activity!!.layoutInflater
        val pickerView = inflater.inflate(R.layout.color_picker, null)

        val lstColors = pickerView.findViewById<View>(R.id.lst_colors) as AbsListView

        val ta = resources.obtainTypedArray(R.array.cell_colors)
        val colors = ArrayList<Int>()
        for (i in 0 until ta.length()) colors.add(ta.getColor(i, Color.TRANSPARENT))
        ta.recycle()

        lstColors.adapter = ColorAdapter(context!!, R.layout.item_color, colors)
        lstColors.setOnItemClickListener { _, view, _, _ ->
            if (colorCallback != null) {
                colorCallback!!.setColor(view.tag as Int)
            }
            this@ColorDialog.dismiss()
        }

        builder.setView(pickerView)
                .setNegativeButton(activity!!.getString(R.string.cancel)) { _, _ -> this@ColorDialog.dialog.cancel() }
        return builder.create()
    }

    internal inner class ColorAdapter(context: Context, resource: Int, objects: List<Int>) : ArrayAdapter<Int>(context, resource, objects) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = LayoutInflater.from(context)

            val color = getItem(position)!!

            val rootView = inflater.inflate(R.layout.item_color, null)
            rootView.tag = color

            val viwColor = rootView.findViewById<View>(R.id.viw_color)
            viwColor.setBackgroundColor(color)

            return rootView
        }
    }

    interface ColorDialogCallback {
        fun setColor(color: Int)
    }

    companion object {

        fun instance(): ColorDialog {
            return ColorDialog()
        }
    }
}
