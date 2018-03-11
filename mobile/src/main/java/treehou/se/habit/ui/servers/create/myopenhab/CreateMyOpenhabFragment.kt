package treehou.se.habit.ui.servers.create.myopenhab


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.OnClick
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_create_my_openhab.*
import kotlinx.android.synthetic.main.fragment_setup_server.*
import treehou.se.habit.R
import treehou.se.habit.module.HasActivitySubcomponentBuilders
import treehou.se.habit.mvp.BaseDaggerFragment
import treehou.se.habit.ui.servers.create.CreateServerActivity
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
class CreateMyOpenhabFragment : BaseDaggerFragment<CreateMyOpenhabContract.Presenter>(), CreateMyOpenhabContract.View {

    @Inject lateinit var myPresenter: CreateMyOpenhabContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_my_openhab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginView.setOnClickListener { login() }
    }

    fun login() {
        myPresenter.login(openhabServerNameText.text.toString(), emailView.text.toString(), passwordView.text.toString())
    }

    override fun showError(error: String) {
        Flowable.just(error)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ errorValue ->
                    errorView.visibility = View.VISIBLE
                    errorView.text = errorValue
                })
    }

    override fun loadServerName(name: String) {
        openhabServerNameText.setText(name)
    }

    override fun loadUsername(name: String) {
        emailView.setText(name)
    }

    override fun loadPassword(password: String) {
        passwordView.setText(password)
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

    override fun getPresenter(): CreateMyOpenhabContract.Presenter? {
        return myPresenter
    }

    override fun injectMembers(hasActivitySubcomponentBuilders: HasActivitySubcomponentBuilders) {
        (hasActivitySubcomponentBuilders.getFragmentComponentBuilder(CreateMyOpenhabFragment::class.java) as CreateMyOpenhabComponent.Builder)
                .fragmentModule(CreateMyOpenhabModule(this))
                .build().injectMembers(this)
    }

    companion object {

        fun newInstance(): CreateMyOpenhabFragment {
            val fragment = CreateMyOpenhabFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(serverId: Long): CreateMyOpenhabFragment {
            val fragment = CreateMyOpenhabFragment()
            val args = Bundle()
            args.putLong(CreateMyOpenhabContract.ARG_SERVER, serverId)
            fragment.arguments = args
            return fragment
        }
    }
}
