package treehou.se.habit.ui.control.cells.config

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import se.treehou.ng.ohcommunicator.connector.models.OHItem
import treehou.se.habit.R
import treehou.se.habit.core.db.model.ItemDB
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.core.db.model.controller.CellDB
import treehou.se.habit.core.db.model.controller.SliderCellDB
import treehou.se.habit.ui.BaseFragment
import treehou.se.habit.ui.util.IconPickerActivity
import treehou.se.habit.util.ConnectionFactory
import treehou.se.habit.util.Util
import java.util.*
import javax.inject.Inject

class CellSliderConfigFragment : BaseFragment() {

    @BindView(R.id.spr_items) lateinit var sprItems: Spinner
    @BindView(R.id.txt_max) lateinit var txtMax: TextView
    @BindView(R.id.btn_set_icon) lateinit var btnSetIcon: ImageButton
    @BindView(R.id.lou_range) lateinit var louRange: View

    @Inject lateinit var connectionFactory: ConnectionFactory

    private var mItemAdapter: ArrayAdapter<OHItem>? = null
    private val items = ArrayList<OHItem>()
    private var sliderCell: SliderCellDB? = null
    private var cell: CellDB? = null
    private var item: OHItem? = null
    private var unbinder: Unbinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Util.getApplicationComponent(this).inject(this)

        if (arguments != null) {
            val id = arguments!!.getLong(ARG_CELL_ID)
            cell = CellDB.load(realm, id)
            sliderCell = cell!!.cellSlider

            if (sliderCell == null) {
                realm.executeTransaction { realm ->
                    sliderCell = SliderCellDB()
                    sliderCell = realm.copyToRealm(sliderCell!!)
                    cell!!.cellSlider = sliderCell
                    realm.copyToRealmOrUpdate(cell!!)
                }
            }

            val itemDB = sliderCell!!.item
            if (itemDB != null) {
                item = itemDB.toGeneric()
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_cell_number_config, container, false)
        unbinder = ButterKnife.bind(this, rootView)

        sprItems.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = items[position]
                if (item != null) {
                    realm.beginTransaction()
                    val itemDB = ItemDB.createOrLoadFromGeneric(realm, item)
                    if (item.type == OHItem.TYPE_NUMBER || item.type == OHItem.TYPE_GROUP) {
                        louRange.visibility = View.VISIBLE
                    } else {
                        louRange.visibility = View.GONE
                    }

                    sliderCell!!.item = itemDB
                    realm.commitTransaction()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        mItemAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_dropdown_item, items)
        sprItems.post { sprItems.adapter = mItemAdapter }
        val servers = realm.where(ServerDB::class.java).findAll()
        items.clear()

        val item = item
        if (item != null) {
            items.add(item)
            mItemAdapter!!.add(item)
            mItemAdapter!!.notifyDataSetChanged()
        }

        for (serverDB in servers) {
            val server = serverDB.toGeneric()
            val serverHandler = connectionFactory.createServerHandler(server, context)
            serverHandler.requestItemsRx()
                    .map<List<OHItem>>({ this.filterItems(it) })
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { items ->
                        this.items.addAll(items)
                        mItemAdapter!!.notifyDataSetChanged()
                    }
        }

        updateIconImage()
        btnSetIcon.setOnClickListener { v ->
            val intent = Intent(activity, IconPickerActivity::class.java)
            startActivityForResult(intent, REQUEST_ICON)
        }

        if (sliderCell != null) {
            txtMax.text = "" + sliderCell!!.max
        } else {
            txtMax.text = 100.toString()
        }

        return rootView
    }

    private fun updateIconImage() {
        btnSetIcon.setImageDrawable(Util.getIconDrawable(activity, sliderCell!!.icon))
    }

    private fun filterItems(items: MutableList<OHItem>): List<OHItem> {

        val tempItems = ArrayList<OHItem>()
        for (item in items) {
            if (item.type == OHItem.TYPE_NUMBER) {
                tempItems.add(item)
            } else if (item.type == OHItem.TYPE_DIMMER) {
                tempItems.add(item)
            } else if (item.type == OHItem.TYPE_COLOR) {
                tempItems.add(item)
            } else if (item.type == OHItem.TYPE_GROUP) {
                tempItems.add(item)
            }
        }
        items.clear()
        items.addAll(tempItems)

        return items
    }

    override fun onPause() {
        super.onPause()

        if (sliderCell!!.item == null) {
            return
        }

        realm.beginTransaction()
        if (sliderCell!!.item.type == OHItem.TYPE_NUMBER || sliderCell!!.item.type == OHItem.TYPE_GROUP) {
            sliderCell!!.min = 0
            sliderCell!!.max = Integer.parseInt(txtMax.text.toString())
        } else {
            sliderCell!!.min = 0
            sliderCell!!.max = 100
        }
        realm.commitTransaction()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder!!.unbind()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_ICON &&
                resultCode == Activity.RESULT_OK &&
                data!!.hasExtra(IconPickerActivity.RESULT_ICON)) {

            val iconName = data.getStringExtra(IconPickerActivity.RESULT_ICON)
            realm.beginTransaction()
            sliderCell!!.icon = if (iconName == "") null else iconName
            realm.commitTransaction()
            updateIconImage()
        }
    }

    companion object {

        private val TAG = "CellSliderConfigFragment"

        private val ARG_CELL_ID = "ARG_CELL_ID"
        private val REQUEST_ICON = 183

        fun newInstance(cell: CellDB): CellSliderConfigFragment {
            val fragment = CellSliderConfigFragment()
            val args = Bundle()
            args.putLong(ARG_CELL_ID, cell.id)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
