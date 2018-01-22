package treehou.se.habit.ui.control.cells.config

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.ToggleButton

import com.trello.rxlifecycle2.components.support.RxFragment

import java.util.ArrayList

import javax.inject.Inject

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import se.treehou.ng.ohcommunicator.connector.models.OHItem
import treehou.se.habit.HabitApplication
import treehou.se.habit.R
import treehou.se.habit.connector.Constants
import treehou.se.habit.core.db.model.ItemDB
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.core.db.model.controller.ButtonCellDB
import treehou.se.habit.core.db.model.controller.CellDB
import treehou.se.habit.util.ConnectionFactory
import treehou.se.habit.util.Util
import treehou.se.habit.ui.util.IconPickerActivity

class CellButtonConfigFragment : RxFragment() {

    @BindView(R.id.spr_items) lateinit var sprItems: Spinner
    @BindView(R.id.tgl_on_off) lateinit var tglOnOff: ToggleButton
    @BindView(R.id.txt_command) lateinit var txtCommand: TextView
    @BindView(R.id.btn_set_icon) lateinit var btnSetIcon: ImageView

    @Inject lateinit var connectionFactory: ConnectionFactory

    private var itemAdapter: ArrayAdapter<OHItem>? = null
    private val items = ArrayList<OHItem>()
    private var item: OHItem? = null
    private var buttonCell: ButtonCellDB? = null
    private var cell: CellDB? = null
    private var realm: Realm? = null
    private var unbinder: Unbinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity!!.application as HabitApplication).component().inject(this)

        realm = Realm.getDefaultInstance()

        if (arguments != null) {
            val id = arguments!!.getLong(ARG_CELL_ID)
            cell = CellDB.load(realm, id)
            buttonCell = cell!!.cellButton

            if (buttonCell == null) {
                realm!!.executeTransaction { realm ->
                    buttonCell = ButtonCellDB()
                    buttonCell!!.command = Constants.COMMAND_ON
                    buttonCell = realm.copyToRealm(buttonCell!!)
                    cell!!.cellButton = buttonCell
                    realm.copyToRealmOrUpdate(cell!!)
                }
            }

            val itemDB = buttonCell!!.item
            if (itemDB != null) {
                item = itemDB.toGeneric()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_cell_button_config, container, false)
        unbinder = ButterKnife.bind(this, rootView)

        sprItems.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                realm!!.beginTransaction()
                val item = items[position]
                if (item != null) {
                    val itemDB = ItemDB.createOrLoadFromGeneric(realm, item)
                    buttonCell!!.item = itemDB
                    when (item.type) {
                        OHItem.TYPE_STRING -> {
                            txtCommand.visibility = View.VISIBLE
                            txtCommand.inputType = InputType.TYPE_CLASS_TEXT
                            tglOnOff.visibility = View.GONE
                        }
                        OHItem.TYPE_NUMBER -> {
                            txtCommand.visibility = View.VISIBLE
                            txtCommand.inputType = InputType.TYPE_CLASS_NUMBER
                            tglOnOff.visibility = View.GONE
                        }
                        OHItem.TYPE_CONTACT -> {
                            txtCommand.visibility = View.GONE
                            tglOnOff.visibility = View.VISIBLE
                        }
                        else -> {
                            txtCommand.visibility = View.GONE
                            tglOnOff.visibility = View.VISIBLE
                        }
                    }
                }
                realm!!.commitTransaction()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        itemAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_dropdown_item, items)
        sprItems.adapter = itemAdapter
        val servers = realm!!.where(ServerDB::class.java).findAll()
        items.clear()

        val item = item
        if (item != null) {
            items.add(item)
            itemAdapter!!.add(item)
            itemAdapter!!.notifyDataSetChanged()
        }

        if (buttonCell!!.item != null) {
            items.add(buttonCell!!.item.toGeneric())
        }
        for (serverDB in servers) {
            val server = serverDB.toGeneric()
            val serverHandler = connectionFactory.createServerHandler(server, activity)
            serverHandler.requestItemsRx()
                    .map<List<OHItem>>({ this.filterItems(it) })
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ items ->
                        this.items.addAll(items)
                        itemAdapter!!.notifyDataSetChanged()
                    }) { Log.e(TAG, "Error fetching switch items") }
        }

        tglOnOff.isChecked = Constants.COMMAND_ON == buttonCell!!.command || Constants.COMMAND_OPEN == buttonCell!!.command
        txtCommand.text = buttonCell!!.command

        updateIconImage()
        btnSetIcon.setOnClickListener {
            val intent = Intent(activity, IconPickerActivity::class.java)
            startActivityForResult(intent, REQUEST_ICON)
        }

        return rootView
    }

    private fun updateIconImage() {
        btnSetIcon.setImageDrawable(Util.getIconDrawable(activity, buttonCell!!.icon))
    }

    private fun filterItems(items: MutableList<OHItem>): List<OHItem> {

        val tempItems = ArrayList<OHItem>()
        for (item in items) {
            if (item.type == OHItem.TYPE_SWITCH ||
                    item.type == OHItem.TYPE_GROUP ||
                    item.type == OHItem.TYPE_STRING ||
                    item.type == OHItem.TYPE_NUMBER ||
                    item.type == OHItem.TYPE_CONTACT ||
                    item.type == OHItem.TYPE_COLOR) {
                tempItems.add(item)
            }
        }
        items.clear()
        items.addAll(tempItems)

        return items
    }

    override fun onPause() {
        super.onPause()

        realm!!.beginTransaction()
        if (buttonCell!!.item == null) {
            buttonCell!!.command = ""
        } else if (buttonCell!!.item.type == OHItem.TYPE_STRING || buttonCell!!.item.type == OHItem.TYPE_NUMBER) {
            buttonCell!!.command = txtCommand.text.toString()
        } else if (buttonCell!!.item.type == OHItem.TYPE_CONTACT) {
            buttonCell!!.command = if (tglOnOff.isChecked) Constants.COMMAND_OPEN else Constants.COMMAND_CLOSE
        } else {
            buttonCell!!.command = if (tglOnOff.isChecked) Constants.COMMAND_ON else Constants.COMMAND_OFF
        }
        realm!!.commitTransaction()
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
            realm!!.beginTransaction()
            buttonCell!!.icon = if (iconName == "") null else iconName
            realm!!.commitTransaction()
            updateIconImage()
        }
    }

    companion object {

        private val TAG = "CellButtonConfig"

        private val ARG_CELL_ID = "ARG_CELL_ID"
        private val REQUEST_ICON = 183

        fun newInstance(cell: CellDB): CellButtonConfigFragment {
            val fragment = CellButtonConfigFragment()
            val args = Bundle()
            args.putLong(ARG_CELL_ID, cell.id)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
