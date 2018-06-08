package treehou.se.habit.ui.bindings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crashlytics.android.Crashlytics
import com.google.gson.reflect.TypeToken
import com.trello.rxlifecycle2.components.support.RxFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_bindings_list.*
import se.treehou.ng.ohcommunicator.connector.models.OHBinding
import se.treehou.ng.ohcommunicator.util.GsonHelper
import treehou.se.habit.R
import treehou.se.habit.connector.models.Binding
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.ui.adapter.BindingAdapter
import treehou.se.habit.util.ConnectionFactory
import treehou.se.habit.util.Constants
import treehou.se.habit.util.Util
import treehou.se.habit.util.logging.Logger
import java.util.*
import javax.inject.Inject

class BindingsFragment : RxFragment() {

    @Inject lateinit var connectionFactory: ConnectionFactory
    @Inject lateinit var logger: Logger

    private var bindingAdapter: BindingAdapter? = null
    private var server: ServerDB? = null
    private var container: ViewGroup? = null

    private var bindings: List<OHBinding> = ArrayList()

    private var realm: Realm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Util.getApplicationComponent(this).inject(this)
        Crashlytics.setString(Constants.FIREABASE_DEBUG_KEY_FRAGMENT, javaClass.name)

        realm = Realm.getDefaultInstance()

        if (arguments != null) {
            if (arguments!!.containsKey(ARG_SERVER)) {
                val serverId = arguments!!.getLong(ARG_SERVER)
                server = Realm.getDefaultInstance().where(ServerDB::class.java).equalTo("id", serverId).findFirst()
            }
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_BINDINGS)) {
                bindings = GsonHelper.createGsonBuilder().fromJson(savedInstanceState.getString(STATE_BINDINGS), object : TypeToken<List<Binding>>() {

                }.type)
            }
        }

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_bindings_list, container, false)

        this.container = container

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setTitle(R.string.bindings)
        setHasOptionsMenu(true)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindingAdapter = BindingAdapter()

        val listener = object : BindingAdapter.ItemClickListener {
            override fun onClick(binding: OHBinding) {
                openBinding(binding)
            }
        }
        bindingAdapter!!.setItemClickListener(listener)

        val gridLayoutManager = GridLayoutManager(activity, 1)
        lstBinding.layoutManager = gridLayoutManager
        lstBinding.itemAnimator = DefaultItemAnimator()
        lstBinding.adapter = bindingAdapter
        bindingAdapter!!.setBindings(bindings)
    }

    /**
     * Open up binding page.
     * @param binding the binding to show.
     */
    private fun openBinding(binding: OHBinding) {
        val fragment = BindingFragment.newInstance(binding)
        activity!!.supportFragmentManager.beginTransaction()
                .replace(this.container!!.id, fragment)
                .addToBackStack(null)
                .commit()
    }

    override fun onResume() {
        super.onResume()
        val serverHandler = connectionFactory.createServerHandler(server!!.toGeneric(), activity)
        serverHandler.requestBindingsRx()
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bindings ->
                    Log.d(TAG, "onUpdate " + bindings)
                    bindingAdapter!!.setBindings(bindings)
                }, { logger.e(TAG, "request bindings failed", it) })
    }

    override fun onDestroy() {
        super.onDestroy()
        realm!!.close()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(STATE_BINDINGS, GsonHelper.createGsonBuilder().toJson(bindings))

        super.onSaveInstanceState(outState)
    }

    companion object {

        private val TAG = BindingsFragment::class.java.simpleName

        private val ARG_SERVER = "ARG_SERVER"

        private val STATE_BINDINGS = "STATE_BINDINGS"

        fun newInstance(serverId: Long): BindingsFragment {
            val fragment = BindingsFragment()
            val args = Bundle()
            args.putLong(ARG_SERVER, serverId)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
