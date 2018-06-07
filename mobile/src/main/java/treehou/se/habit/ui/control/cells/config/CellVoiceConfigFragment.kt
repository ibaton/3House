package treehou.se.habit.ui.control.cells.config

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.trello.rxlifecycle2.components.support.RxFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_cell_voice_config.*
import se.treehou.ng.ohcommunicator.connector.models.OHItem
import treehou.se.habit.R
import treehou.se.habit.core.db.model.ItemDB
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.core.db.model.controller.CellDB
import treehou.se.habit.core.db.model.controller.VoiceCellDB
import treehou.se.habit.ui.util.IconPickerActivity
import treehou.se.habit.util.ConnectionFactory
import treehou.se.habit.util.Util
import treehou.se.habit.util.logging.Logger
import java.util.*
import javax.inject.Inject

class CellVoiceConfigFragment : RxFragment() {

    @Inject lateinit var connectionFactory: ConnectionFactory
    @Inject lateinit var logger: Logger

    private var voiceCell: VoiceCellDB? = null
    private var cell: CellDB? = null

    private var item: OHItem? = null

    private var itemAdapter: ArrayAdapter<OHItem>? = null
    private val items = ArrayList<OHItem>()

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Util.getApplicationComponent(this).inject(this)

        realm = Realm.getDefaultInstance()
        itemAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_dropdown_item, items)
        if (arguments != null) {
            val id = arguments!!.getLong(ARG_CELL_ID)
            cell = CellDB.load(realm, id)
            voiceCell = cell!!.getCellVoice()
            if (voiceCell == null) {
                realm.executeTransaction { realm ->
                    voiceCell = VoiceCellDB()
                    voiceCell = realm.copyToRealm(voiceCell!!)
                    cell!!.setCellVoice(voiceCell!!)
                    realm.copyToRealmOrUpdate(cell)
                }
            }

            val itemDB = voiceCell!!.item
            if (itemDB != null) {
                item = itemDB.toGeneric()
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cell_voice_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemsSpinner.adapter = itemAdapter

        val servers = realm.where(ServerDB::class.java).findAll()
        items.clear()

        for (serverDB in servers) {
            val server = serverDB.toGeneric()
            val serverHandler = connectionFactory.createServerHandler(server, context)
            serverHandler.requestItemsRx()
                    .map<List<OHItem>>({ this.filterItems(it) })
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ items ->
                        this.items.addAll(items)
                        itemAdapter!!.notifyDataSetChanged()
                    }, { logger.e(TAG, "Failed to load items", it) })
        }

        itemsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = items[position]
                realm.beginTransaction()
                val itemDB = ItemDB.createOrLoadFromGeneric(realm, item)
                voiceCell!!.item = itemDB
                realm.commitTransaction()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val item = item
        if (item != null) {
            items.add(item)
            itemAdapter!!.add(item)
            itemAdapter!!.notifyDataSetChanged()
        }

        updateIconImage()
        setIconButton.setOnClickListener {
            val intent = Intent(activity, IconPickerActivity::class.java)
            startActivityForResult(intent, REQUEST_ICON)
        }
    }

    private fun filterItems(items: MutableList<OHItem>): List<OHItem> {

        val tempItems = ArrayList<OHItem>()
        for (item in items) {
            if (item.type == OHItem.TYPE_STRING) {
                tempItems.add(item)
            }
        }
        items.clear()
        items.addAll(tempItems)

        return items
    }

    override fun onDestroy() {
        super.onDestroy()

        realm.close()
    }

    private fun updateIconImage() {
        setIconButton.setImageDrawable(Util.getIconDrawable(activity, voiceCell!!.icon))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_ICON &&
                resultCode == Activity.RESULT_OK &&
                data!!.hasExtra(IconPickerActivity.RESULT_ICON)) {

            realm.beginTransaction()
            val iconName = data.getStringExtra(IconPickerActivity.RESULT_ICON)
            voiceCell!!.icon = if (iconName == "") null else iconName
            updateIconImage()
            realm.commitTransaction()
        }
    }

    companion object {

        private val TAG = "CellVoiceConfigFragment"

        private val ARG_CELL_ID = "ARG_CELL_ID"
        private val REQUEST_ICON = 183

        fun newInstance(cell: CellDB): CellVoiceConfigFragment {
            val fragment = CellVoiceConfigFragment()
            val args = Bundle()
            args.putLong(ARG_CELL_ID, cell.id)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
