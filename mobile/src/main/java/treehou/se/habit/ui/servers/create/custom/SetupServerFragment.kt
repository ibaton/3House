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
import treehou.se.habit.R
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.module.HasActivitySubcomponentBuilders
import treehou.se.habit.mvp.BaseDaggerFragment
import treehou.se.habit.ui.servers.create.CreateServerActivity
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
    @BindView(R.id.btn_save)
    @JvmField
    var btnSave: Button? = null

    @BindView(R.id.top_label)
    @JvmField
    var topLabel: View? = null

    private var unbinder: Unbinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getPresenter(): SetupServerContract.Presenter? {
        return presenter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_setup_server, container, false)
        unbinder = ButterKnife.bind(this, rootView)

        return rootView
    }

    override fun showTopLabel(show: Boolean) {
        topLabel?.visibility = if (show) View.VISIBLE else View.GONE
    }

    @OnClick(R.id.btn_save)
    internal fun onSave() {
        save()
    }

    override fun onResume() {
        super.onResume()

        RxTextView.textChanges(remoteUrlText!!)
                .compose(bindToLifecycle())
                .subscribe { text -> errorRemoteUrlText!!.visibility = if (text.length <= 0 || Patterns.WEB_URL.matcher(text).matches()) View.GONE else View.VISIBLE }

        RxTextView.textChanges(localUrlText!!)
                .compose(bindToLifecycle())
                .subscribe { text -> errorLocalUrlText!!.visibility = if (text.length <= 0 || Patterns.WEB_URL.matcher(text).matches()) View.GONE else View.VISIBLE }
    }

    override fun loadServer(server: ServerDB) {
        txtName!!.setText(server.name)
        localUrlText!!.setText(server.localUrl)
        remoteUrlText!!.setText(server.remoteUrl)
        txtUsername!!.setText(server.username)
        txtPassword!!.setText(server.password)
    }

    private fun toUrl(text: String): String {

        val uri = Uri.parse(text)
        return uri.toString()
    }

    private fun save() {
        val server = ServerData(txtName!!.text.toString(), toUrl(localUrlText!!.text.toString()), toUrl(remoteUrlText!!.text.toString()), txtUsername!!.text.toString(), txtPassword!!.text.toString())

        presenter?.saveServer(server)
    }

    /**
     * Close this window
     */
    override fun closeWindow() {
        val currentActivity = activity
        if (currentActivity is CreateServerActivity) {
            activity?.finish()
        } else {
            fragmentManager?.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder!!.unbind()
    }

    override fun injectMembers(hasActivitySubcomponentBuilders: HasActivitySubcomponentBuilders) {
        (hasActivitySubcomponentBuilders.getFragmentComponentBuilder(SetupServerFragment::class.java) as SetupServerComponent.Builder)
                .fragmentModule(SetupServerModule(this))
                .build().injectMembers(this)
    }

    companion object {

        fun newInstance(): SetupServerFragment {
            val fragment = SetupServerFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(serverId: Long): SetupServerFragment {
            val fragment = SetupServerFragment()
            val args = Bundle()
            args.putLong(SetupServerPresenter.ARG_SERVER, serverId)
            fragment.arguments = args
            return fragment
        }
    }
}
