package treehou.se.habit.ui.control

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_slider.*
import se.treehou.ng.ohcommunicator.connector.models.OHItem
import treehou.se.habit.BaseActivity
import treehou.se.habit.HabitApplication
import treehou.se.habit.R
import treehou.se.habit.core.db.model.controller.CellDB
import treehou.se.habit.core.db.model.controller.SliderCellDB
import treehou.se.habit.ui.BaseFragment
import treehou.se.habit.util.ConnectionFactory
import treehou.se.habit.util.Util
import javax.inject.Inject

class SliderActivity : BaseActivity() {

    @Inject lateinit var connectionFactory: ConnectionFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Util.getApplicationComponent(this).inject(this)
        setContentView(R.layout.activity_slider)

        val id = intent.extras!!.getLong(ARG_CELL)
        val sliderFragment = SliderFragment.newInstance(id)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, sliderFragment, SLIDER_TAG)
                    .commit()
        }

        container.setOnClickListener { closeClick() }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    internal fun closeClick() {
        finish()
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class SliderFragment : BaseFragment() {

        private var sliederCell: SliderCellDB? = null
        private var sbrNumber: SeekBar? = null
        private var itemName: TextView? = null

        @Inject lateinit var connectionFactory: ConnectionFactory

        internal var sliderListener: SeekBar.OnSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (sliederCell != null) {
                    val server = sliederCell!!.item!!.server!!.toGeneric()
                    val serverHandler = connectionFactory.createServerHandler(server, context)
                    serverHandler.sendCommand(sliederCell!!.item!!.name, "" + seekBar.progress)
                }
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            (activity!!.application as HabitApplication).component().inject(this)
            if (arguments != null) {
                val id = arguments!!.getLong(ARG_CELL)
                logger.d(TAG, "Loading cell " + id)
                val cell = CellDB.load(realm, id)
                sliederCell = cell!!.getCellSlider()
            }
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            try {
                val rootView = inflater.inflate(R.layout.fragment_slider, container, false)

                itemName = rootView.findViewById(R.id.item_name)
                sbrNumber = rootView.findViewById(R.id.sbrNumber)
                sbrNumber!!.max = sliederCell!!.max
                sbrNumber!!.setOnSeekBarChangeListener(sliderListener)
                return rootView
            } catch (e: Exception) {
                logger.e(TAG, "Slider adapter inflater fail", e)
                return inflater.inflate(R.layout.item_widget_null, container, false)
            }

        }

        override fun onResume() {
            super.onResume()

            val server = sliederCell!!.item!!.server!!.toGeneric()
            val serverHandler = connectionFactory.createServerHandler(server, context)
            serverHandler.requestItemRx(sliederCell!!.item!!.name)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose<OHItem>(bindToLifecycle<OHItem>())
                    .subscribe({ ohItem ->
                        try {
                            val context = context
                            if (ohItem?.state != null && context != null) {
                                if (ohItem.getLabel() != null) {
                                    itemName!!.visibility = View.VISIBLE
                                    itemName!!.text = Util.createLabel(context, ohItem.label)
                                } else {
                                    itemName!!.visibility = View.GONE
                                }

                                sbrNumber!!.setOnSeekBarChangeListener(null)

                                val progress = ohItem.state.toFloatOrNull() ?: 0f

                                sbrNumber!!.progress = progress.toInt()
                                sbrNumber!!.setOnSeekBarChangeListener(sliderListener)
                            }
                        } catch (e: Exception) {
                            logger.e(TAG, "Failed to update progress", e)
                        }
                    }) { logger.e(TAG, "Error getting slider data", it) }
        }

        companion object {

            fun newInstance(id: Long): SliderFragment {
                val fragment = SliderFragment()
                val args = Bundle()
                args.putLong(ARG_CELL, id)
                fragment.arguments = args
                return fragment
            }
        }
    }

    companion object {
        val TAG = "SliderActivity"

        val ACTION_NUMBER = "active"
        val ARG_CELL = "arg_cell"

        val SLIDER_TAG = "sliderDialog"
    }
}
