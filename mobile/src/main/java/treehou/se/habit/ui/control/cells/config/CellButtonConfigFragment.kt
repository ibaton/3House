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
import com.trello.rxlifecycle2.components.support.RxFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_cell_button_config.*
import se.treehou.ng.ohcommunicator.connector.models.OHItem
import treehou.se.habit.HabitApplication
import treehou.se.habit.R
import treehou.se.habit.connector.Constants
import treehou.se.habit.core.db.model.ItemDB
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.core.db.model.controller.ButtonCellDB
import treehou.se.habit.core.db.model.controller.CellDB
import treehou.se.habit.ui.util.IconPickerActivity
import treehou.se.habit.util.ConnectionFactory
import treehou.se.habit.util.Util
import java.util.*
import javax.inject.Inject

class CellButtonConfigFragment : RxFragment() {

    @Inject lateinit var connectionFactory: ConnectionFactory

    private var itemAdapter: ArrayAdapter<OHItem>? = null
    private val items = ArrayList<OHItem>()
    private var item: OHItem? = null
    private var buttonCell: ButtonCellDB? = null
    private var cell: CellDB? = null
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity!!.application as HabitApplication).component().inject(this)

        realm = Realm.getDefaultInstance()

        if (arguments != null) {
            val id = arguments!!.getLong(ARG_CELL_ID)
            cell = CellDB.load(realm, id)
            buttonCell = cell!!.getCellButton()

            if (buttonCell == null) {
                realm!!.executeTransaction { realm ->
                    buttonCell = ButtonCellDB()
                    buttonCell!!.command = Constants.COMMAND_ON
                    buttonCell = realm.copyToRealm(buttonCell!!)
                    cell!!.setCellButton(buttonCell!!)
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

        return inflater.inflate(R.layout.fragment_cell_button_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                realm!!.beginTransaction()
                val item = items[position]
                if (item != null) {
                    val itemDB = ItemDB.createOrLoadFromGeneric(realm!!, item)
                    buttonCell!!.item = itemDB
                    when (item.type) {
                        OHItem.TYPE_STRING -> {
                            commandText.visibility = View.VISIBLE
                            commandText.inputType = InputType.TYPE_CLASS_TEXT
                            toggleOnOff.visibility = View.GONE
                        }
                        OHItem.TYPE_NUMBER -> {
                            commandText.visibility = View.VISIBLE
                            commandText.inputType = InputType.TYPE_CLASS_NUMBER
                            toggleOnOff.visibility = View.GONE
                        }
                        OHItem.TYPE_CONTACT -> {
                            commandText.visibility = View.GONE
                            toggleOnOff.visibility = View.VISIBLE
                        }
                        else -> {
                            commandText.visibility = View.GONE
                            toggleOnOff.visibility = View.VISIBLE
                        }
                    }
                }
                realm!!.commitTransaction()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        itemAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_dropdown_item, items)
        itemsSpinner.adapter = itemAdapter
        val servers = realm!!.where(ServerDB::class.java).findAll()
        items.clear()

        val item = item
        if (item != null) {
            items.add(item)
            itemAdapter!!.add(item)
            itemAdapter!!.notifyDataSetChanged()
        }

        if (buttonCell!!.item != null) {
            items.add(buttonCell!!.item!!.toGeneric())
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

        toggleOnOff.isChecked = Constants.COMMAND_ON == buttonCell!!.command || Constants.COMMAND_OPEN == buttonCell!!.command
        commandText.setText(buttonCell!!.command)

        updateIconImage()
        setIconButton.setOnClickListener {
            val intent = Intent(activity, IconPickerActivity::class.java)
            startActivityForResult(intent, REQUEST_ICON)
        }
    }

    private fun updateIconImage() {
        setIconButton.setImageDrawable(Util.getIconDrawable(activity, buttonCell!!.icon))
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
        } else if (buttonCell!!.item!!.type == OHItem.TYPE_STRING || buttonCell!!.item!!.type == OHItem.TYPE_NUMBER) {
            buttonCell!!.command = commandText.text.toString()
        } else if (buttonCell!!.item!!.type == OHItem.TYPE_CONTACT) {
            buttonCell!!.command = if (toggleOnOff.isChecked) Constants.COMMAND_OPEN else Constants.COMMAND_CLOSE
        } else {
            buttonCell!!.command = if (toggleOnOff.isChecked) Constants.COMMAND_ON else Constants.COMMAND_OFF
        }
        realm!!.commitTransaction()
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
