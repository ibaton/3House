package treehou.se.habit.ui.servers.create.custom

import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.RxTextView
import kotlinx.android.synthetic.main.fragment_setup_server.*
import treehou.se.habit.R
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.dagger.HasActivitySubcomponentBuilders
import treehou.se.habit.dagger.fragment.SetupServerComponent
import treehou.se.habit.dagger.fragment.SetupServerModule
import treehou.se.habit.mvp.BaseDaggerFragment
import treehou.se.habit.ui.servers.create.CreateServerActivity
import javax.inject.Inject

class SetupServerFragment : BaseDaggerFragment<SetupServerContract.Presenter>(), SetupServerContract.View {

    @Inject
    lateinit var serverPresenter: SetupServerContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getPresenter(): SetupServerContract.Presenter? {
        return serverPresenter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_setup_server, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnSave.setOnClickListener { onSave() }
    }

    internal fun onSave() {
        save()
    }

    override fun onResume() {
        super.onResume()

        RxTextView.textChanges(remoteUrlText)
                .compose(bindToLifecycle())
                .subscribe ({ text -> errorRemoteUrlText.visibility = if (text.length <= 0 || Patterns.WEB_URL.matcher(text).matches()) View.GONE else View.VISIBLE },
                        {logger.e(TAG, "Update text remoteUrlText failed", it)})

        RxTextView.textChanges(localUrlText)
                .compose(bindToLifecycle())
                .subscribe ({ text -> errorLocalUrlText.visibility = if (text.length <= 0 || Patterns.WEB_URL.matcher(text).matches()) View.GONE else View.VISIBLE },
                    {logger.e(TAG, "Update text localUrlText failed", it)})
    }

    override fun loadServer(server: ServerDB) {
        serverNameText.setText(server.name)
        localUrlText.setText(server.localurl)
        remoteUrlText.setText(server.remoteurl)
        txtUsername.setText(server.username)
        txtPassword.setText(server.password)
    }

    private fun toUrl(text: String): String {

        val uri = Uri.parse(text)
        return uri.toString()
    }

    private fun save() {
        val server = ServerData(serverNameText.text.toString(), toUrl(localUrlText.text.toString()), toUrl(remoteUrlText.text.toString()), txtUsername.text.toString(), txtPassword.text.toString())

        serverPresenter.saveServer(server)
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

    override fun injectMembers(hasActivitySubcomponentBuilders: HasActivitySubcomponentBuilders) {
        (hasActivitySubcomponentBuilders.getFragmentComponentBuilder(SetupServerFragment::class.java) as SetupServerComponent.Builder)
                .fragmentModule(SetupServerModule(this))
                .build().injectMembers(this)
    }

    companion object {

        val TAG = "SetupServerFragment"

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
