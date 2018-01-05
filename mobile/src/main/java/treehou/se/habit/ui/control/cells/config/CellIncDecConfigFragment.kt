package treehou.se.habit.ui.control.cells.config

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner

import com.trello.rxlifecycle2.components.support.RxFragment

import java.util.ArrayList

import javax.inject.Inject

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import se.treehou.ng.ohcommunicator.connector.models.OHItem
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.services.IServerHandler
import treehou.se.habit.R
import treehou.se.habit.core.db.model.ItemDB
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.core.db.model.controller.CellDB
import treehou.se.habit.core.db.model.controller.IncDecCellDB
import treehou.se.habit.util.ConnectionFactory
import treehou.se.habit.util.Constants
import treehou.se.habit.util.Util
import treehou.se.habit.ui.util.IconPickerActivity

class CellIncDecConfigFragment : RxFragment() {

    @BindView(R.id.spr_items) lateinit var sprItems: Spinner
    @BindView(R.id.txtMax) lateinit var txtMax: EditText
    @BindView(R.id.txtMin) lateinit var txtMin: EditText
    @BindView(R.id.txtValue) lateinit var txtValue: EditText
    @BindView(R.id.btn_set_icon) lateinit var btnSetIcon: ImageButton

    @Inject lateinit var connectionFactory: ConnectionFactory
    lateinit var realm: Realm

    private var itemAdapter: ArrayAdapter<OHItem>? = null
    private val items = ArrayList<OHItem>()

    private var incDecCell: IncDecCellDB? = null
    private var cell: CellDB? = null
    private var item: OHItem? = null
    private var unbinder: Unbinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Util.getApplicationComponent(this).inject(this)
        realm = Realm.getDefaultInstance()

        if (arguments != null) {
            val id = arguments!!.getLong(ARG_CELL_ID)
            cell = CellDB.load(realm, id)
            incDecCell = cell!!.cellIncDec

            if (incDecCell == null) {
                realm.executeTransaction { realm ->
                    incDecCell = IncDecCellDB()
                    incDecCell = realm.copyToRealm(incDecCell!!)
                    cell!!.cellIncDec = incDecCell
                    realm.copyToRealmOrUpdate(cell!!)
                }
            }

            val itemDB = incDecCell!!.item
            if (itemDB != null) {
                item = itemDB.toGeneric()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.inc_dec_controller_action, container, false)
        unbinder = ButterKnife.bind(this, rootView)

        txtMax.setText("" + incDecCell!!.max)
        txtMin.setText("" + incDecCell!!.min)
        txtValue.setText("" + incDecCell!!.value)

        sprItems.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                realm.beginTransaction()
                val item = items[position]
                if (item != null) {
                    val itemDB = ItemDB.createOrLoadFromGeneric(realm, item)
                    incDecCell!!.item = itemDB
                }
                realm.commitTransaction()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        itemAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_dropdown_item, items)
        sprItems.adapter = itemAdapter
        val servers = realm.where(ServerDB::class.java).findAll()
        items.clear()

        val item = item
        if (item != null) {
            items.add(item)
            itemAdapter!!.add(item)
            itemAdapter!!.notifyDataSetChanged()
        }

        if (incDecCell!!.item != null) {
            items.add(incDecCell!!.item.toGeneric())
        }
        for (serverDB in servers) {
            val server = serverDB.toGeneric()
            val serverHandler = connectionFactory.createServerHandler(server, context)
            serverHandler.requestItemsRx()
                    .map<List<OHItem>>({ this.filterItems(it) })
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { newItems ->
                        this.items.addAll(newItems)
                        itemAdapter!!.notifyDataSetChanged()
                    }
        }

        updateIconImage()
        btnSetIcon.setOnClickListener { v ->
            val intent = Intent(activity, IconPickerActivity::class.java)
            startActivityForResult(intent, REQUEST_ICON)
        }

        return rootView
    }

    private fun updateIconImage() {
        btnSetIcon.setImageDrawable(Util.getIconDrawable(activity, incDecCell!!.icon))
    }

    private fun filterItems(items: MutableList<OHItem>): List<OHItem> {

        val tempItems = ArrayList<OHItem>()
        for (item in items) {
            if (Constants.SUPPORT_INC_DEC.contains(item.type)) {
                tempItems.add(item)
            }
        }
        items.clear()
        items.addAll(tempItems)

        return items
    }

    override fun onPause() {
        super.onPause()

        realm.beginTransaction()
        try {
            incDecCell!!.max = Integer.parseInt(txtMax.text.toString())
        } catch (e: NumberFormatException) {
            incDecCell!!.max = 100
        }

        try {
            incDecCell!!.min = Integer.parseInt(txtMin.text.toString())
        } catch (e: NumberFormatException) {
            incDecCell!!.min = 0
        }

        try {
            incDecCell!!.value = Integer.parseInt(txtValue.text.toString())
        } catch (e: NumberFormatException) {
            incDecCell!!.value = 1
        }

        realm.commitTransaction()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder!!.unbind()
    }

    override fun onDestroy() {
        super.onDestroy()

        realm.close()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_ICON &&
                resultCode == Activity.RESULT_OK &&
                data!!.hasExtra(IconPickerActivity.RESULT_ICON)) {

            val iconName = data.getStringExtra(IconPickerActivity.RESULT_ICON)
            realm.beginTransaction()
            incDecCell!!.icon = if (iconName == "") null else iconName
            realm.commitTransaction()
            updateIconImage()
        }
    }

    companion object {

        private val TAG = "CellIncDecConfigFragment"

        private val ARG_CELL_ID = "ARG_CELL_ID"
        private val REQUEST_ICON = 183

        fun newInstance(cell: CellDB): CellIncDecConfigFragment {
            val fragment = CellIncDecConfigFragment()
            val args = Bundle()
            args.putLong(ARG_CELL_ID, cell.id)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
