package treehou.se.habit.ui.control

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.LinearLayout
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_edit_control.*
import treehou.se.habit.HabitApplication
import treehou.se.habit.R
import treehou.se.habit.core.db.model.controller.ControllerDB
import treehou.se.habit.ui.colorpicker.ColorDialog
import treehou.se.habit.ui.control.cells.config.ControllCellFragment
import treehou.se.habit.ui.homescreen.ControllerWidget
import javax.inject.Inject
import javax.inject.Named

class EditControlFragment : Fragment(), ColorDialog.ColorDialogCallback {

    @Inject
    lateinit var controllerUtil: ControllerUtil
    @Inject
    @field:Named("config")
    lateinit var cellFactory: CellFactory

    private lateinit var actionBar: ActionBar
    @JvmField
    var controller: ControllerDB? = null
    private lateinit var activity: AppCompatActivity

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        (context!!.applicationContext as HabitApplication).component().inject(this)
        super.onCreate(savedInstanceState)

        realm = Realm.getDefaultInstance()

        activity = getActivity() as AppCompatActivity

        if (arguments != null) {
            val id = arguments!!.getLong(ARG_ID)
            controller = ControllerDB.load(realm, id)
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_control, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actionBar = activity.supportActionBar!!

        updateColorPalette(controller!!.color)

        btnAddRow.setOnClickListener {
            controller!!.addRow(realm)
            Log.d("Controller", "Added controller, currently " + controller?.cellRows?.size + " rows")
            redrawController()
        }
        redrawController()

        val i = Intent("treehou.se.UPDATE_WIDGET")
        getActivity()!!.sendBroadcast(i)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.edit_controllers, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId) {
            R.id.action_more -> openExtraSettings()
        }

        return super.onOptionsItemSelected(item)
    }

    fun openExtraSettings() {
        val intent = Intent(getActivity(), EditControllerSettingsActivity::class.java)
        val extras = Bundle()

        extras.putLong(ARG_ID, controller!!.id)
        intent.putExtras(extras)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        startActivity(intent)
        getActivity()!!.overridePendingTransition(0, 0)
    }

    override fun onResume() {
        super.onResume()

        redrawController()
    }

    override fun onPause() {
        super.onPause()

        val intent = Intent(getActivity(), ControllerWidget::class.java)
        intent.action = "android.appwidget.action.APPWIDGET_UPDATE"
        val activity = getActivity()
        val ids = AppWidgetManager.getInstance(activity!!.application).getAppWidgetIds(ComponentName(activity.application, ControllerWidget::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        getActivity()!!.sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        realm.close()
    }

    fun redrawController() {
        buttonHolderLayout.removeAllViews()
        val inflater = LayoutInflater.from(getActivity())
        Log.d(TAG, "Drawing controller " + controller!!.cellRows.size)
        for (row in controller!!.cellRows) {
            val louRow = inflater.inflate(R.layout.controller_row_edit, buttonHolderLayout, false) as LinearLayout

            val rowParam = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            rowParam.weight = 1f
            louRow.layoutParams = rowParam

            val louColumnHolder = louRow.findViewById<View>(R.id.buttonHolderLayout) as LinearLayout
            val btnAddCell = louRow.findViewById<View>(R.id.btn_add_column) as ImageButton

            for (cell in row.cells) {
                Log.d(TAG, "Drawing cell " + cell.id)

                val activity = getActivity()
                if (activity != null) {
                    val itemView = cellFactory.create(activity, buttonHolderLayout, controller!!, cell)

                    itemView.setOnClickListener {
                        getActivity()!!.supportFragmentManager.beginTransaction()
                                .replace(R.id.page_container, ControllCellFragment.newInstance(cell.id))
                                .addToBackStack(null)
                                .commit()
                    }

                    itemView.setOnLongClickListener {
                        AlertDialog.Builder(activity)
                                .setMessage(activity!!.getString(R.string.delete_cell))
                                .setPositiveButton(R.string.ok) { dialog, which ->
                                    realm.beginTransaction()
                                    cell.deleteFromRealm()
                                    if (row.cells.size <= 0) row.deleteFromRealm()
                                    realm.commitTransaction()
                                    redrawController()
                                }
                                .setNegativeButton(R.string.cancel, null)
                                .show()

                        true
                    }
                    louColumnHolder.addView(itemView)
                }
            }

            btnAddCell.setOnClickListener {
                row.addCell(realm)
                redrawController()
            }
            buttonHolderLayout.addView(louRow)
        }
    }

    /**
     * Update ui to match color set.
     *
     * @param color the color to use as base.
     */
    fun updateColorPalette(color: Int) {

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
            view.getWindow().setStatusBarColor(pallete[0]);
            view.getWindow().setNavigationBarColor(pallete[0]);
            if(actionBar != null) {
                actionBar.setBackgroundDrawable(new ColorDrawable(pallete[0]));
            }
        }*/

        redrawController()
    }

    override fun onDestroyView() {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            activity.window.statusBarColor = activity.resources.getColor(R.color.colorPrimaryDark)
            activity.window.navigationBarColor = activity.resources.getColor(R.color.navigationBarColor)
            actionBar.setBackgroundDrawable(ColorDrawable(activity.resources.getColor(R.color.colorPrimary)))
        }

        super.onDestroyView()
    }

    override fun setColor(color: Int) {
        controller!!.color = color
        updateColorPalette(color)
    }

    companion object {

        private val TAG = "EditControlFragment"

        val ARG_ID = "ARG_ID"

        val REQUEST_COLOR = 3117

        fun newInstance(id: Long): EditControlFragment {
            val fragment = EditControlFragment()
            val args = Bundle()

            args.putLong(ARG_ID, id)

            fragment.arguments = args
            return fragment
        }
    }
}
