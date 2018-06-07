package treehou.se.habit.ui.control

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.mattyork.colours.Colour
import io.realm.Realm
import treehou.se.habit.HabitApplication
import treehou.se.habit.R
import treehou.se.habit.core.db.model.controller.ControllerDB
import treehou.se.habit.util.Util
import javax.inject.Inject
import javax.inject.Named

class ControlFragment : Fragment() {

    private lateinit var louController: LinearLayout

    private var controller: ControllerDB? = null
    @Inject
    @field:Named("display")
    lateinit var cellFactory: CellFactory

    private var actionBar: ActionBar? = null
    private var activity: AppCompatActivity? = null

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        (context!!.applicationContext as HabitApplication).component().inject(this)
        super.onCreate(savedInstanceState)

        realm = Realm.getDefaultInstance()
        if (arguments != null) {
            val id = arguments!!.getLong(ARG_ID)
            controller = ControllerDB.load(realm, id)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        activity = getActivity() as AppCompatActivity?
        actionBar = activity!!.supportActionBar

        val pallete: IntArray
        if (Colour.alpha(controller!!.color) < 100) {
            pallete = Util.generatePallete(resources.getColor(R.color.colorPrimary))
        } else {
            pallete = Util.generatePallete(controller!!.color)
        }

        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_contoll, container, false)

        val viwBackground = rootView.findViewById<View>(R.id.backgroundView)

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            activity!!.window.statusBarColor = pallete[0]
            activity!!.window.navigationBarColor = pallete[0]
            if (actionBar != null) {
                actionBar!!.setBackgroundDrawable(ColorDrawable(pallete[0]))
            }
        }
        if (actionBar != null) {
            actionBar!!.title = controller!!.name
        }

        viwBackground.setBackgroundColor(pallete[0])

        louController = rootView.findViewById<View>(R.id.lou_rows) as LinearLayout
        redrawController()

        return rootView
    }

    override fun onDestroyView() {

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            activity!!.window.statusBarColor = activity!!.resources.getColor(R.color.colorPrimaryDark)
            activity!!.window.navigationBarColor = activity!!.resources.getColor(R.color.navigationBarColor)
            if (actionBar != null) {
                actionBar!!.setBackgroundDrawable(ColorDrawable(activity!!.resources.getColor(R.color.colorPrimary)))
            }
        }

        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()

        realm!!.close()
    }

    fun redrawController() {

        louController.removeAllViews()
        val inflater = LayoutInflater.from(getActivity())

        for (row in controller!!.cellRows) {
            val louRow = inflater.inflate(R.layout.controller_row, louController, false) as LinearLayout
            val rowParam = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            rowParam.weight = 1f
            louRow.layoutParams = rowParam

            val louColumnHolder = louRow.findViewById<View>(R.id.buttonHolderLayout) as LinearLayout
            for (cell in row.cells) {
                val activity = getActivity()
                val controller = controller
                if (activity != null && controller != null) {
                    val itemView = cellFactory.create(activity, louColumnHolder, controller, cell)
                    louColumnHolder.addView(itemView)
                }
            }
            louController.addView(louRow)
        }
    }

    companion object {

        val TAG = "ControlFragment"
        val ARG_ID = "ARG_ID"

        fun newInstance(id: Long): ControlFragment {
            val fragment = ControlFragment()
            val args = Bundle()
            args.putLong(ARG_ID, id)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
