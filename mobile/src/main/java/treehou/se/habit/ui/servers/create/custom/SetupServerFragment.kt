package treehou.se.habit.ui.servers.create.custom

import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.jakewharton.rxbinding2.widget.RxTextView
import io.realm.Realm
import treehou.se.habit.R
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.module.HasActivitySubcomponentBuilders
import treehou.se.habit.mvp.BaseDaggerFragment
import javax.inject.Inject

class SetupServerFragment : BaseDaggerFragment<SetupServerContract.Presenter>(), SetupServerContract.View {

    @Inject
    @JvmField
    var presenter: SetupServerContract.Presenter? = null

    @BindView(R.id.server_name_text)
    @JvmField
    var txtName: EditText? = null
    @BindView(R.id.server_local_text)
    @JvmField
    var localUrlText: EditText? = null
    @BindView(R.id.error_local_url)
    @JvmField
    var errorLocalUrlText: TextView? = null
    @BindView(R.id.txt_server_remote)
    @JvmField
    var remoteUrlText: EditText? = null
    @BindView(R.id.error_remote_url)
    @JvmField
    var errorRemoteUrlText: TextView? = null
    @BindView(R.id.txt_username)
    @JvmField
    var txtUsername: EditText? = null
    @BindView(R.id.txt_password)
    @JvmField
    var txtPassword: EditText? = null
    @BindView(R.id.btn_back)
    @JvmField
    var btnBack: Button? = null

    private var serverId: Long = -1
    private var buttonTextId = R.string.back
    private var unbinder: Unbinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments

        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_SERVER_ID)) {
            serverId = savedInstanceState.getLong(EXTRA_SERVER_ID)
        } else if (bundle != null) {
            if (bundle.containsKey(ARG_SERVER)) serverId = bundle.getLong(ARG_SERVER)
            buttonTextId = bundle.getInt(ARG_BUTTON_TEXT_ID, R.string.back)
        }
    }

    override fun getPresenter(): SetupServerContract.Presenter? {
        return presenter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_setup_server, container, false)
        unbinder = ButterKnife.bind(this, rootView)

        btnBack!!.setText(buttonTextId)

        return rootView
    }

    @OnClick(R.id.btn_back)
    internal fun onBack() {
        activity!!.supportFragmentManager.popBackStack()
    }

    override fun onResume() {
        super.onResume()

        val realm = Realm.getDefaultInstance()
        val server = realm.where(ServerDB::class.java).equalTo("id", serverId).findFirst()
        if (server != null) {
            txtName!!.setText(server.name)
            localUrlText!!.setText(server.localUrl)
            remoteUrlText!!.setText(server.remoteUrl)
            txtUsername!!.setText(server.username)
            txtPassword!!.setText(server.password)
        }
        realm.close()

        RxTextView.textChanges(remoteUrlText!!)
                .compose(bindToLifecycle())
                .subscribe { text -> errorRemoteUrlText!!.visibility = if (text.length <= 0 || Patterns.WEB_URL.matcher(text).matches()) View.GONE else View.VISIBLE }

        RxTextView.textChanges(localUrlText!!)
                .compose(bindToLifecycle())
                .subscribe { text -> errorLocalUrlText!!.visibility = if (text.length <= 0 || Patterns.WEB_URL.matcher(text).matches()) View.GONE else View.VISIBLE }
    }

    private fun toUrl(text: String): String {

        val uri = Uri.parse(text)
        return uri.toString()
    }

    override fun onPause() {
        super.onPause()

        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { realm1 ->
            val server = ServerDB()
            if (serverId <= 0) {
                server.id = ServerDB.getUniqueId()
                serverId = server.id
            } else {
                server.id = serverId
            }
            server.name = txtName!!.text.toString()
            server.localUrl = toUrl(localUrlText!!.text.toString())
            server.remoteUrl = toUrl(remoteUrlText!!.text.toString())
            server.username = txtUsername!!.text.toString()
            server.password = txtPassword!!.text.toString()
            realm1.copyToRealmOrUpdate(server)
        }
        realm.close()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder!!.unbind()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putLong(EXTRA_SERVER_ID, serverId)
        super.onSaveInstanceState(outState)
    }

    override fun injectMembers(hasActivitySubcomponentBuilders: HasActivitySubcomponentBuilders) {
        (hasActivitySubcomponentBuilders.getFragmentComponentBuilder(SetupServerFragment::class.java) as SetupServerComponent.Builder)
                .fragmentModule(SetupServerModule(this))
                .build().injectMembers(this)
    }

    companion object {

        private val ARG_SERVER = "ARG_SERVER"
        val ARG_BUTTON_TEXT_ID = "ARG_BUTTON_TEXT_ID"

        private val EXTRA_SERVER_ID = "EXTRA_SERVER_ID"

        fun newInstance(): SetupServerFragment {
            val fragment = SetupServerFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(serverId: Long): SetupServerFragment {
            val fragment = SetupServerFragment()
            val args = Bundle()
            args.putLong(ARG_SERVER, serverId)
            fragment.arguments = args
            return fragment
        }
    }
}
